package zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnZip {
	final static int BUFFER = 2048;

	public static void main(final String argv[]) {
		try {
			BufferedOutputStream dest = null;
			final FileInputStream fis = new FileInputStream(argv[0]);
			final ZipInputStream zis = new ZipInputStream(
					new BufferedInputStream(fis));
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				System.out.println("Extracting: " + entry);
				int count;
				final byte data[] = new byte[BUFFER];
				// write the files to the disk
				final FileOutputStream fos = new FileOutputStream(entry
						.getName());
				dest = new BufferedOutputStream(fos, BUFFER);
				while ((count = zis.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			}
			zis.close();
		} catch (final Exception e) {
			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
		}
	}
}
