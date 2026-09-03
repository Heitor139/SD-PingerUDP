import java.io.*;
import java.net.*;

/*
 * Servidor para processar as requisições de Ping sobre UDP.
 */
public class PingClient {
    private static DatagramSocket socket;

    public static void main(String[] args) throws Exception {
        // Obter o argumento da linha de comando.
        if (args.length != 2) {
            System.out.println("Required arguments: host, port");
            return;
        }
        
        InetAddress host = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);
        
        
        // Socket criado FORA do loop para não causar erro (BindException) na segunda mensagem
        socket = new DatagramSocket();
        
        Double minimum = Double.MAX_VALUE, maximum = Double.MIN_VALUE;
        Double mean = 0.0;
        Integer count = 0;

        for (Integer i = 0; i < 10; i++) {
            byte[] dadosEnvio = new byte[1024];
            byte[] dadosRecebidos = new byte[1024];
            

            Long timestamp = System.nanoTime();
            String s = "PING " + i.toString() + " " + timestamp.toString();
            dadosEnvio = s.getBytes();
            // Criar um pacote de datagrama para comportar o pacote UDP de chegada.
            DatagramPacket request = new DatagramPacket(dadosEnvio, dadosEnvio.length, host, port);
            
            // Bloquear até que o hospedeiro receba o pacote UDP.
            socket.send(request);
            DatagramPacket receive = new DatagramPacket(dadosRecebidos, dadosRecebidos.length);

            try {
                socket.setSoTimeout(1000);
                socket.receive(receive);
                printData(receive);

                Double time = (System.nanoTime() - timestamp) / 1_000_000.0;
                mean += time;
                count++;

                minimum = Double.min(minimum, time);
                maximum = Double.max(maximum, time);

            } catch (SocketTimeoutException e) {
                System.out.println("Pacote perdido");
            } 

        }

        mean = mean / count;

        // printar os valores RTT - Questão 1

        System.out.println("skjdfnasjdfg");

        System.out.printf("Minimum: %fms\n", minimum);
        System.out.printf("Maximum: %fms\n", maximum);
        System.out.printf("Média: %fms\n", mean);
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