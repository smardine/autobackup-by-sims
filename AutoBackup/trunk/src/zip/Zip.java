package zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zip {
	static final int BUFFER = 2048;

	public static void main(final String argv[]) {
		try {
			BufferedInputStream origin = null;
			final FileOutputStream dest = new FileOutputStream(
					"./archive/mail.zip");
			final CheckedOutputStream checksum = new CheckedOutputStream(dest,
					new Adler32());
			final ZipOutputStream out = new ZipOutputStream(
					new BufferedOutputStream(checksum));
			// out.setMethod(ZipOutputStream.DEFLATED);
			final byte data[] = new byte[BUFFER];
			// get a list of files from current directory
			final File f = new File("./corrigés");
			final String files[] = f.list();
			// for (int i=0; i<f.length(); i++) {
			for (int i = 0; i < files.length; i++) {
				System.out.println("Adding: " + files[i]);
				final FileInputStream fi = new FileInputStream(files[i]);
				origin = new BufferedInputStream(fi, BUFFER);
				final ZipEntry entry = new ZipEntry(files[i]);
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}
			out.close();
			System.out
					.println("checksum: " + checksum.getChecksum().getValue());
		} catch (final Exception e) {
			Utilitaires.Historique.ecrire("Message d'erreur: " + e);
		}
	}
}
