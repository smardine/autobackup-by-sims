package zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnZip2 {
	static final int BUFFER = 2048;

	@SuppressWarnings("unchecked")
	public static void main(final String argv[]) {
		try {
			BufferedOutputStream dest = null;
			BufferedInputStream is = null;
			ZipEntry entry;
			final ZipFile zipfile = new ZipFile(argv[0]);
			final Enumeration e = zipfile.entries();
			while (e.hasMoreElements()) {
				entry = (ZipEntry) e.nextElement();
				System.out.println("Extracting: " + entry);
				is = new BufferedInputStream(zipfile.getInputStream(entry));
				int count;
				final byte data[] = new byte[BUFFER];
				final FileOutputStream fos = new FileOutputStream(entry
						.getName());
				dest = new BufferedOutputStream(fos, BUFFER);
				while ((count = is.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
				is.close();
			}
		} catch (final Exception e) {
			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
		}
	}
}
