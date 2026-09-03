import java.net.*;

public class ReliableUdpSender {
    private DatagramSocket socket;
    private InetAddress destAddress;
    private int destPort;

    public ReliableUdpSender(String host, int port) throws Exception {
        // iniciar o datagram socket
    }

    public void sendData(String message) throws Exception {
        boolean ackRecebido = false;

        while (!ackRecebido) {
            try {
                
                
            } catch (SocketTimeoutException e) {
                
            }
        }
    }
}