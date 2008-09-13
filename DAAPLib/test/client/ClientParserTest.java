package client;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import reader.DACPResponseParser;


public class ClientParserTest {

        DACPResponseParser client;

        @Before
        public void setup() {
                client = new DACPResponseParser();
        }

        @Test
        public void parseTestArtist() {
                System.out.println("Parse Artist:");
                byte[] message = {'c', 'm', 's', 't', 0, 0, 0, 18, // cmst + size
                                'c', 'a', 'n', 'a', 0, 0, 0, 10, // cana + size
                                'T', 'o', 'm', '_', 'L', 'e', 'h', 'r', 'e','r'}; // Tom_Lehrer
                InputStream s = new ByteArrayInputStream(message);
                System.out.println(client.parse(s));
                System.out.println();
        }

        @Test
        public void parseTestCash() {
                System.out.println("Parse Boolean:");
                byte[] message = {'c', 'm', 's', 't', 0, 0, 0, 9, // cmst + size
                                'c', 'a', 's', 'h', 0, 0, 0, 1, // cash + size
                                1}; // 1
                InputStream s = new ByteArrayInputStream(message);
                System.out.println(client.parse(s));
                System.out.println();
        }

        @Test
        public void parseTestArtistCash() {
                System.out.println("Parse Artist & Boolean:");
                byte[] message = {'c', 'm', 's', 't', 0, 0, 0, 21, // cmst + size
                                'c', 'a', 's', 'h', 0, 0, 0, 1, // cash + size
                                1,
                                'c', 'a', 'n', 'a', 0, 0, 0, 9, // cana + size
                                'T', 'o', 'm', '_', 'L', 'e', 'h', 'r', 'e','r'}; // Tom_Lehrer};
                InputStream s = new ByteArrayInputStream(message);
                System.out.println(client.parse(s));
                System.out.println();
        }

        @Test
        public void parseLong() {
                System.out.println("Parse Long:");
                byte[] message = {'c', 'm', 's', 't', 0, 0, 0, 21, // cmst + size
                                'c', 'a', 'n', 'p', 0, 0, 0, 16, // canp + size
                                0, 0, 0, 0, 0, 0, 0, 14,
                                0, 0, 0, 0, 0, 0, 0, 1}; // 01
                InputStream s = new ByteArrayInputStream(message);
                System.out.println(client.parse(s));
                System.out.println();
        }

        @Test
        public void parseSmallPlaylist() {
                System.out.println("Parse Small Playlist:");
                byte[] message = {'a', 'p', 's', 'o', 0, 0, 0, 44,
                                'm', 'l', 'c', 'l', 0, 0, 0, 36,
                                'm', 'l', 'i', 't', 0, 0, 0, 28,
                                'a', 's', 'a', 'r', 0, 0, 0, 6,
                                'M', 'i', 'n', 'u', 'i', 't',
                                'm', 'i', 'n', 'm', 0, 0, 0, 6,
                                'S', 'o', 'v', 'i', 'e', 't'
                                };
                InputStream s = new ByteArrayInputStream(message);
                System.out.println(client.parse(s));
                System.out.println();
        }

        @Test
        public void parsePlaylist() {
                System.out.println("Parse Playlist:");
                byte[] message = {'a', 'p', 's', 'o', 0, 0, 0, 91,
                                'm', 'l', 'c', 'l', 0, 0, 0, 83,
                                'm', 'l', 'i', 't', 0, 0, 0, 39,
                                'a', 's', 'a', 'r', 0, 0, 0, 6,
                                'M', 'i', 'n', 'u', 'i', 't',
                                'm', 'i', 'n', 'm', 0, 0, 0, 17,
                                'S', 'o', 'v', 'i', 'e', 't', '_', 'A', 'i', 'r', 'h', 'o', 's', 't', 'e', 's', 's',
                                'm', 'l', 'i', 't', 0, 0, 0, 28,
                                'a', 's', 'a', 'r', 0, 0, 0, 6,
                                'M', 'i', 'n', 'u', 'i', 't',
                                'm', 'i', 'n', 'm', 0, 0, 0, 6,
                                'C', 'l', 'a', 'i', 'r', 'e'};
                InputStream s = new ByteArrayInputStream(message);
                System.out.println(client.parse(s));
                System.out.println();
        }

        @Test
        public void parseMultipleTracksPlaylist() {
                System.out.println("Parse Multiple Track Playlist:");
                byte[] message = {
'a', 'p', 's', 'o', 0, 0, 0, (byte)114,           		
	'm', 'l', 'c', 'l', 0, 0, 0, 59,
		'm', 'l', 'i', 't', 0, 0, 0, 31,
			'a', 's', 'a', 'r', 0, 0, 0, 6,
				'M', 'i', 'n', 'u', 'i', 't',
			'm', 'i', 'n', 'm', 0, 0, 0, 17,
                'S', 'o', 'v', 'i', 'e', 't', '_', 'A', 'i', 'r', 'h', 'o', 's', 't', 'e', 's', 's',
        'm', 'l', 'i', 't', 0, 0, 0, 20,
        	'a', 's', 'a',  'r', 0, 0, 0, 6,
        		'M', 'i', 'n', 'u', 'i', 't',
        	'm', 'i', 'n', 'm', 0, 0, 0, 6,
        		'C', 'l', 'a', 'i', 'r', 'e',
	'm', 'l', 'c', 'l', 0, 0, 0, 47,
   		'm', 'l', 'i', 't', 0, 0, 0, 17,
   			'a', 's', 'a', 'r', 0, 0, 0, 5,
   				'J', 'o', 'r', 'i', 's',
   			'm', 'i', 'n', 'm', 0, 0, 0, 4,
                'C', 'o', 'o', 'l',
        'm', 'l', 'i', 't', 0, 0, 0, 22,
           	'a', 's', 'a', 'r', 0, 0, 0, 7,
           		'W', 'i', 'l', 'l', 'i', 'a', 'm',
           	'm', 'i', 'n', 'm', 0, 0, 0, 7,
           		'E', 'x', 't', 'r', 'e', 'm', 'e'
                                

};
                
                
                InputStream s = new ByteArrayInputStream(message);
                System.out.println(client.parse(s));
                System.out.println();
        }
        
        @Test
        public void parseBrokenPlaylist() {
                System.out.println("Parse Broken Playlist:");
                byte[] message = {'a', 'p', 's', 'o', 0, 0, 0, 91,
                                'm', 'l', 'c', 'l', 0, 0, 0, 83,
                                'm', 'l', 'i', 't', 0, 0, 0, 39,
                                'a', 's', 'a', 'r', 0, 0, 0, 6,
                                'M', 'i', 'n', 'u', 'i', 't',
                                'm', 'i', 'n', 's', 0, 0, 0, 17,
                                'S', 'o', 'v', 'i', 'e', 't', '_', 'A', 'i', 'r', 'h', 'o', 's', 't', 'e', 's', 's',
                                'm', 'l', 'i', 't', 0, 0, 0, 28,
                                'a', 's', 'a', 'r', 0, 0, 0, 6,
                                'M', 'i', 'n', 'u', 'i', 't',
                                'm', 'i', 'n', 'm', 0, 0, 0, 6,
                                'C', 'l', 'a', 'i', 'r', 'e'};
                InputStream s = new ByteArrayInputStream(message);
                System.out.println(client.parse(s));
                System.out.println();
        }

}
