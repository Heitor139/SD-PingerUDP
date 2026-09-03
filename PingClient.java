import java.io.*;
import java.net.*;
import java.util.*;

public class PingClient {
    private static DatagramSocket socket;

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Required arguments: host port");
            return;
        }
        
        InetAddress host = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);
        
        
        socket = new DatagramSocket();

        Timer timer = new Timer();

        TimerTask repeatTask = new TimerTask() {
            int count = 0;
            int pacotesRecebidos = 0;
            
            double minimum = Double.MAX_VALUE;
            double maximum = Double.MIN_VALUE;
            double somaRTT = 0.0;

            @Override
            public void run() {
                try {
                    long timestamp = System.nanoTime();
                    
                    String mensagem = "PING " + count + " " + timestamp + "\r\n";
                    byte[] dadosEnvio = mensagem.getBytes();
                    
                    DatagramPacket request = new DatagramPacket(dadosEnvio, dadosEnvio.length, host, port);
                    socket.send(request);
                    
                    byte[] dadosRecebidos = new byte[1024];
                    DatagramPacket receive = new DatagramPacket(dadosRecebidos, dadosRecebidos.length);
            
                    socket.receive(receive);
                    printData(receive);
        
                    double rtt = (System.nanoTime() - timestamp) / 1_000_000.0;
                    
                    if (rtt < minimum) minimum = rtt;
                    if (rtt > maximum) maximum = rtt;
                    somaRTT += rtt;
                    pacotesRecebidos++;
        
                } catch (SocketTimeoutException e) {
                    System.out.println("Ping " + count + ": Pacote perdido (Timeout)\n");
                } catch (Exception e) {
                    System.out.println("Erro inesperado: " + e.getMessage());
                }

                count++;

                if (count == 10) {
                    timer.cancel();
                    
                    System.out.println("--- Estatísticas do Ping UDP ---");
                    System.out.println("Pacotes: Enviados = 10, Recebidos = " + pacotesRecebidos + 
                                       ", Perdidos = " + (10 - pacotesRecebidos));
                    
                    if (pacotesRecebidos > 0) {
                        double media = somaRTT / pacotesRecebidos;
                        System.out.printf("RTT Mínimo: %.2f ms\n", minimum);
                        System.out.printf("RTT Máximo: %.2f ms\n", maximum);
                        System.out.printf("RTT Médio:  %.2f ms\n", media);
                    }
                    
                    socket.close();
                }
            }
        };

        System.out.println("Iniciando PING para " + host.getHostAddress() + " na porta " + port);
        
        // Questão 2: Envia exatamente 1 Ping por segundo (delay=0, periodo=1000ms)
        timer.schedule(repeatTask, 0, 1000);
    }

    /*
     * Imprimir o dado de Ping para o trecho de saída padrão.
     */
    private static void printData(DatagramPacket request) throws Exception {
        // Obter referências para a ordem de pacotes de bytes.
        byte[] buf = request.getData();
        
        // Envolver os bytes numa cadeia de entrada vetor de bytes, de modo que
        // você possa ler os dados como uma cadeia de bytes.
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        
        // Envolver a cadeia de saída do vetor bytes num leitor de cadeia de
        // entrada, de modo que você possa ler os dados como uma cadeia de caracteres.
        InputStreamReader isr = new InputStreamReader(bais);
        
        // Envolver o leitor de cadeia de entrada num leitor com armazenagem, de
        // modo que você possa ler os dados de caracteres linha a linha. (A
        // linha é uma sequência de caracteres terminados por alguma combinação de \r e \n.)
        BufferedReader br = new BufferedReader(isr);
        
        // O dado da mensagem está contido numa única linha, então leia esta linha.
        String line = br.readLine();
        
        // Imprimir o endereço do hospedeiro e o dado recebido dele.
        System.out.println("Received from " + request.getAddress().getHostAddress() + ":" + line);
    }
}

