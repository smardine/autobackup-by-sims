/*
 * FileUtility
 * @author P. Thomas
 * -----------------------------------------------------------------------------
 * Description Les quatres principales methodes de cette classe utilitaire
 * permet : - de calculer un chemin relatif à partir d'un répertoire de
 * référence et du répertoire à relativiser - de supprimer récursivement des
 * répertoires - de copier un fichier - de créer un fichier à un emplacement
 * précis avec un nom défini à l'aide d'un pattern du type
 * "Out-{0,number,#000}.tmp" [Cf. MessageFormat]
 * -----------------------------------------------------------------------------
 */

package Utilitaires;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;

import javax.swing.JFileChooser;

public class FileUtility {

	@SuppressWarnings("unchecked")
	/**
	 * retourne le path complet
	 * @param ref -File le fichier
	 * @param access -File 
	 * @return path -String
	 */
	protected static String getPrimaryPath(final File ref, final File access) {
		String path = "";
		final ArrayList refp = new ArrayList();
		for (File cur = ref; cur != null; cur = cur.getParentFile()) {
			String name = cur.getName();
			if (name.length() == 0) {
				name = cur.getAbsolutePath();
			}
			refp.add(name);
			// System.out.println("Ref : '" + name + "'");
		}
		final ArrayList accp = new ArrayList();
		for (File cur = access; cur != null; cur = cur.getParentFile()) {
			String name = cur.getName();
			if (name.length() == 0) {
				name = cur.getAbsolutePath();
			}
			accp.add(name);
			// System.out.println("Acc : '" + name + "'");
		}
		if (refp.size() == 0 || accp.size() == 0) {
			return path;
		}

		if (refp.get(refp.size() - 1).equals(accp.get(accp.size() - 1))) {
			boolean equal = true;
			while (equal && refp.size() > 1 && accp.size() > 1) {
				refp.remove(refp.size() - 1);
				accp.remove(accp.size() - 1);
				equal = (refp.get(refp.size() - 1).equals(accp
						.get(accp.size() - 1)));
			}

			if (refp.size() == 1) {
				if (!equal) {
					refp.remove(refp.size() - 1);
					path += "..";
				} else {
					refp.remove(refp.size() - 1);
					accp.remove(accp.size() - 1);
					path += ".";
				}
			} else {
				if (equal && accp.size() == 1) {
					refp.remove(refp.size() - 1);
					accp.remove(accp.size() - 1);
				}
				while (refp.size() > 0) {
					refp.remove(refp.size() - 1);
					path += "..";
					if (refp.size() > 0) {
						path += File.separator;
					}
				}
			}
			while (accp.size() > 0) {
				final String name = (String) accp.remove(accp.size() - 1);
				path += File.separator;
				path += name;
			}
		} else {
			try {
				path = access.getCanonicalPath();
			} catch (final IOException e) {
				path = access.getAbsolutePath();
				Historique.ecrire("InvocationTargetException " + e);
			}
		}
		return path;
	}

	/**
	 * retourne le path differentiel
	 * @param ref -File le fichier
	 * @param acc -File
	 */
	public static String getDiffPath(final File ref, final File acc) {
		try {
			final File cref = ref.getCanonicalFile();
			final File cacc = acc.getCanonicalFile();
			return getPrimaryPath(cref, cacc);
		} catch (final IOException e) {
			Historique.ecrire("InvocationTargetException " + e);
			return getPrimaryPath(ref, acc);
		}
	}

	/**
	 * retourne le path differentiel
	 * @param ref -String le fichier
	 * @param acc -String
	 */
	public static String getDiffPath(final String ref, final String acc) {
		final File fref = new File(ref);
		final File facc = new File(acc);
		return getDiffPath(fref, facc);
	}

	public static void main(final String[] args) {
		final JFileChooser dlg = new JFileChooser();
		dlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		while (true) {
			if (dlg.showOpenDialog(null) == JFileChooser.CANCEL_OPTION) {
				return;
			}
			final File first = dlg.getSelectedFile();
			if (first == null) {
				return;
			}
			if (dlg.showOpenDialog(null) == JFileChooser.CANCEL_OPTION) {
				return;
			}
			final File last = dlg.getSelectedFile();
			if (last == null) {
				return;
			}

			System.out.println("First Path : " + first.getPath());
			System.out.println("Last  Path : " + last.getPath());
			System.out.println("First Name : " + first.getName());
			System.out.println("Last  Name : " + last.getName());
			System.out.println("First AbsolutePath : "
					+ first.getAbsolutePath());
			System.out
					.println("Last  AbsolutePath : " + last.getAbsolutePath());

			System.out.println("Diff  PrimaryPath : "
					+ getPrimaryPath(first, last));
			System.out
					.println("--------------------------------------------------\n");
		}
	}

	/**
	 * supprime recurssivement un repertoire
	 * @param path -File le repertoire a supprimer
	 * @throws IOException
	 */
	public static void recursifDelete(final File path) throws IOException {
		if (!path.exists()) {
			throw new IOException("File not found '" + path.getAbsolutePath()
					+ "'");
		}

		if (path.isDirectory()) {
			final File[] children = path.listFiles();
			for (int i = 0; children != null && i < children.length; i++) {
				recursifDelete(children[i]);
			}
			if (!path.delete()) {
				throw new IOException("No delete path '"
						+ path.getAbsolutePath() + "'");

			}
		} else if (!path.delete()) {
			throw new IOException("No delete file '" + path.getAbsolutePath()
					+ "'");
		}
	}

	/**
	 * copie de fichier
	 * @param src -File la source
	 * @param dest -File la destination
	 * @throws IOException
	 */
	public static void copyFile(final File src, final File dest)
			throws IOException {
		if (!src.exists()) {
			throw new IOException("File not found '" + src.getAbsolutePath()
					+ "'");
		}
		final BufferedOutputStream out = new BufferedOutputStream(
				new FileOutputStream(dest));
		final BufferedInputStream in = new BufferedInputStream(
				new FileInputStream(src));

		final byte[] read = new byte[128];
		int len = 128;
		while ((len = in.read(read)) > 0) {
			out.write(read, 0, len);
		}

		out.flush();
		out.close();
		in.close();
	}

	/**
	 * création d'un fichier en particulier
	 * @return File
	 * @param path -String chemin du fichier
	 * @param pattern -String extension
	 * @param base -int
	 * @throws IOException
	 */
	public static File createSpecifiedFile(final String path,
			final String pattern, final int base) {
		File file = new File(path);
		if (!file.isDirectory()) {
			return null;
		}

		for (int cur = base;; cur++) {
			final Object[] args = { new Integer(cur) };
			final String result = MessageFormat.format(pattern, args);
			file = new File(path, result);
			if (!file.exists()) {
				return file;
			}
		}
	}
}
