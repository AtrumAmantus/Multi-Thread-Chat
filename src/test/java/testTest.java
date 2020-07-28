import lombok.RequiredArgsConstructor;
import org.junit.Test;

import java.nio.ByteBuffer;

public class testTest {

    @RequiredArgsConstructor
    enum OpCode {
        TEXT(1),
        BINARY(2),
        CLOSE(8),
        PING(9),
        PONG(10),
        UNKNOWN(-1);

        private final int opCode;

        public static OpCode getValueOf(int value) {
            OpCode opCode = UNKNOWN;

            for (OpCode opCode1 : OpCode.values()) {
                if (opCode1.opCode == value) {
                    opCode = opCode1;
                }
            }

            return opCode;
        }
    }

    @Test
    public void test() {
        byte[] encodedData = {-127,-98,116,-124,24,7,15,-90,126,117,27,-23,58,61,86,-59,108,117,1,-23,58,43,86,-16,125,127,0,-90,34,37,0,-31,107,115,86,-7};
        ByteBuffer decodedData;

        OpCode opCode = OpCode.getValueOf(encodedData[0] & 15);
        boolean isMasked = (encodedData[1] & 128) == 128;
        int payloadLength = encodedData[1] & 127;

        if (isMasked) {
            if (!OpCode.UNKNOWN.equals(opCode)) {
                byte[] mask;
                int payloadOffset;
                long dataLength = 0;
                if (payloadLength == 126) {
                    mask = new byte[] { encodedData[4], encodedData[5], encodedData[6], encodedData[7] };
                    payloadOffset = 8;
                    dataLength = ((encodedData[2] << 8) | encodedData[3]) + payloadLength;
                } else if (payloadLength == 127) {
                    mask = new byte[] { encodedData[10], encodedData[11], encodedData[12], encodedData[13] };
                    payloadOffset = 14;
                    for (int i = 0; i < 8; ++i) {
                        int leftShift = 8 - i;
                        dataLength += (encodedData[i] << leftShift);
                    }
                    dataLength += payloadOffset;
                } else {
                    mask = new byte[] { encodedData[2], encodedData[3], encodedData[4], encodedData[5] };
                    payloadOffset = 6;
                    dataLength = payloadLength + payloadOffset;
                }

                if (encodedData.length < dataLength) {
                    decodedData = ByteBuffer.allocate(0);
                } else {
                        decodedData = ByteBuffer.allocate((int) dataLength);
                        for (int i = payloadOffset; i < dataLength; ++i) {
                            if (isMasked) {
                                int j = i - payloadOffset;
                                if (i < encodedData.length) {
                                    decodedData.put((byte)(encodedData[i] ^ mask[j % 4]));
                                }
                            } else {
                                decodedData.put(encodedData[i]);
                            }
                        }
                    //decodedData = new byte[0];
                }
                int i = 0;
            } else {
                System.out.println("Can't proceed, Invalid opcode.");
                decodedData = ByteBuffer.allocate(0);
            }
        } else {
            System.out.println("Can't proceed, unmasked.");
            decodedData = ByteBuffer.allocate(0);
        }
        System.out.println(new String(decodedData.array()));
    }

}
