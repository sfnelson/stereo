package test;

import java.io.IOException;
import java.io.InputStream;

public class StreamPrinter {

	public static void print(InputStream input) {
		if (!input.markSupported()) {
			System.out.println("Mark not supported");
			return;
		}
		input.mark(0);
		try {
			while (input.available() > 0) {
				switch (input.available()) {
				default:
					System.out.printf("%02x", input.read());
				case 3:
					System.out.printf("%02x", input.read());
				case 2:
					System.out.printf("%02x", input.read());
				case 1:
					System.out.printf("%02x", input.read());
				case 0:
					System.out.printf(" ");
				}
			}
			System.out.println();
			input.reset();
			input.mark(0);
			while (input.available() > 0) {
				switch (input.available()) {
				default:
					System.out.printf("%c ", input.read());
				case 3:
					System.out.printf("%c ", input.read());
				case 2:
					System.out.printf("%c ", input.read());
				case 1:
					System.out.printf("%c ", input.read());
				case 0:
					System.out.printf(" ");
				}
			}
			System.out.println();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
