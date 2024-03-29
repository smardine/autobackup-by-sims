package zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import Utilitaires.Historique;
import Utilitaires.ParseString;

/**
 * Classe d'utilitaires pour les compressions et d�compression Zip, GZip, JAR...
 * @author iubito (Sylvain Machefert)
 */
public class OutilsZip {

	/** Taille du buffer pour les lectures/�critures */
	private static final int BUFFER_SIZE = 8 * 1024;
	private static int NbFIchierDejaZipp� = 0;

	/**
	 * D�compresse un GZIP contenant un fichier unique. Efface le filedest avant
	 * de commencer.
	 * @param gzsource Fichier GZIP � d�compresser
	 * @param filedest Nom du fichier destination o� sera sauvegard� le fichier
	 *            contenu dans le GZIP.
	 * @throws FileNotFoundException si le fichier GZip n'existe pas
	 * @throws IOException
	 * @see http://javaalmanac.com/egs/java.util.zip/UncompressFile.html?l=rel
	 */
	public static void gunzip(final String gzsource, final String filedest)
			throws FileNotFoundException, IOException {
		// Open the compressed file
		final GZIPInputStream in = new GZIPInputStream(new FileInputStream(
				gzsource));
		try {
			final BufferedInputStream bis = new BufferedInputStream(in);
			try {
				// Open the output file
				final OutputStream out = new FileOutputStream(filedest);
				try {
					final BufferedOutputStream bos = new BufferedOutputStream(
							out);
					try {
						// Transfer bytes from the compressed file to the output
						// file
						byte[] buf = new byte[BUFFER_SIZE];
						int len;
						while ((len = bis.read(buf, 0, BUFFER_SIZE)) != -1) {
							bos.write(buf, 0, len);
						}
						buf = null;
					} finally {
						bos.close();
					}
				} finally {
					out.close();
				}
			} finally {
				bis.close();
			}
		} finally {
			in.close();
		}
	}

	/**
	 * Compresse un fichier dans un GZIP. Efface le filedest avant de commencer.
	 * @param filesource Fichier � compresser
	 * @param gzdest Fichier GZIP cible
	 * @throws FileNotFoundException si le fichier source n'existe pas ou si le
	 *             GZIP n'existe pas apr�s la compression
	 * @throws IOException
	 * @see http://javaalmanac.com/egs/java.util.zip/CompressFile.html?l=rel
	 * @see http://java.developpez.com/livres/penserenjava/?chap=12&page=3
	 */
	public static boolean gzip(final String filesource, final String gzdest)
			throws FileNotFoundException, IOException {
		// Create the GZIP output stream
		final GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(
				gzdest));
		try {
			final BufferedOutputStream bos = new BufferedOutputStream(out);
			try {
				// Open the input file
				final FileInputStream in = new FileInputStream(filesource);
				try {
					final BufferedInputStream bis = new BufferedInputStream(in);
					try {
						// Transfer bytes from the input file to the GZIP output
						// stream
						byte[] buf = new byte[BUFFER_SIZE];
						int len;
						while ((len = bis.read(buf, 0, BUFFER_SIZE)) > 0) {
							bos.write(buf, 0, len);
						}
						buf = null;
					} finally {
						bis.close();
					}
				} finally {
					in.close();
				}
			} finally {
				bos.close();
			}
		} finally {
			out.close();
		}
		if (!new File(gzdest).exists()) {
			// throw new FileNotFoundException("Le fichier " + gzdest +
			// " n'a pas �t� cr��");
			System.out.println("Le fichier " + gzdest + " n'a pas �t� cr��");
			return false;
		}
		return true;
	}

	/**
	 * D�compresse l'archive Zip dans un r�pertoire. R�utilise les noms des
	 * r�pertoires lors de la d�compression.
	 * @param zipsrc
	 * @param basedirdest
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws SecurityException
	 */
	public static void unzipToDir(final String zipsrc, final String basedirdest)
			throws FileNotFoundException, IOException, SecurityException {

		unzipToDir(new FileInputStream(zipsrc), basedirdest);
	}

	/**
	 * D�compresse le flux <tt>InputStream</tt> Zip dans un r�pertoire.
	 * R�utilise les noms des r�pertoires lors de la d�compression.
	 * @param zipsrc
	 * @param basedirdest
	 * @throws SecurityException
	 * @throws IOException
	 */
	public static void unzipToDir(final InputStream inzip,
			final String basedirdest) throws IOException, SecurityException {

		final File base = new File(basedirdest);
		if (!base.exists()) {
			base.mkdirs();
		}

		try {
			final CheckedInputStream checksum = new CheckedInputStream(inzip,
					new Adler32());
			try {
				// Buffer sur le zip
				final BufferedInputStream bis = new BufferedInputStream(
						checksum);
				try {
					final ZipInputStream zis = new ZipInputStream(bis);
					try {
						ZipEntry entry;
						File f;
						int count;
						final byte[] buf = new byte[BUFFER_SIZE];
						BufferedOutputStream bos;
						FileOutputStream fos;
						// Parcours les entr�es du zip
						while ((entry = zis.getNextEntry()) != null) {
							f = new File(basedirdest, entry.getName());
							if (entry.isDirectory()) {
								f.mkdirs();
							} else { // L'entry semble �tre un fichier
								// Si contient un / on cr�e les r�pertoires, car
								// parfois on a pas dir/ avant dir/fichier.ext
								final int l = entry.getName().lastIndexOf('/');
								if (l != -1) {
									new File(basedirdest, entry.getName()
											.substring(0, l)).mkdirs();
								}
								fos = new FileOutputStream(f);
								try {
									bos = new BufferedOutputStream(fos,
											BUFFER_SIZE);
									try {
										// Ecriture du fichier
										while ((count = zis.read(buf, 0,
												BUFFER_SIZE)) != -1) {
											bos.write(buf, 0, count);
										}
									} finally {
										bos.close();
									}
								} finally {
									fos.close();
								}
							}
							if (entry.getTime() != -1) {
								f.setLastModified(entry.getTime());
							}
						}
					} finally {
						zis.close();
					}
				} finally {
					bis.close();
				}
			} finally {
				checksum.close();
			}
		} finally {
			inzip.close();
		}
	}

	/**
	 * Zippe r�cursivement un r�pertoire, en mettant les chemins de fichiers en
	 * relatif.
	 * <p>
	 * Les accents des noms de fichiers sont supprim�s, voir
	 * {@link OutilsString#sansAccents(String)} pour plus de d�tails.
	 * @param dirsource Le r�pertoire � compresser
	 * @param zipdest Le nom du fichier zip r�sultat
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static boolean zipDir(final String dirsource, final String zipdest,
			final int NbFIchierAZipper, final JProgressBar ProgressTotale,
			final JProgressBar Progress, final JLabel MessageUtilisateur)
			throws FileNotFoundException, IOException {

		// Cr�ation d'un flux d'�criture vers un fichier
		final FileOutputStream fos = new FileOutputStream(zipdest);
		try {
			// Ajout du checksum : Adler32 (plus rapide) ou CRC32
			final CheckedOutputStream checksum = new CheckedOutputStream(fos,
					new Adler32());
			try {
				// Cr�ation d'un buffer de sortie afin d'am�liorer les
				// performances
				final BufferedOutputStream bos = new BufferedOutputStream(
						checksum, BUFFER_SIZE);
				try {
					// Cr�ation d'un flux d'�criture Zip vers le fichier �
					// travers le buffer
					final ZipOutputStream zos = new ZipOutputStream(bos);
					try {
						// Compression maximale
						try {
							zos.setMethod(ZipOutputStream.DEFLATED);
							zos.setLevel(Deflater.BEST_COMPRESSION);
						} catch (final Exception ignor) {
							System.out.println(ignor);
							Historique
									.ecrire("Erreur lors de l'archivage du fichier : "
											+ dirsource
											+ " vers le chemin : "
											+ zipdest);
							Historique
									.ecrire("Message de l'api zip : " + ignor);
							return false;
						}
						zipDirDoubleProgressBar(dirsource, null, zos,
								NbFIchierAZipper, ProgressTotale, Progress,
								MessageUtilisateur);
					} finally {
						zos.close();
					}
				} finally {
					bos.close();
				}
			} finally {
				checksum.close();
			}
		} finally {
			fos.close();
		}
		return true;
	}

	/**
	 * �tant donn� un r�pertoire base (qui n'est pas inclu dans les entr�es
	 * <tt>ZipEntry</tt>), le r�pertoire courant � zipper, et le
	 * <tt>ZipOutputStream</tt> de sortie, ajoute les fichiers dans le zip, ou
	 * s'appelle r�cursivement pour ajouter un r�pertoire fils.
	 * @param basedir
	 * @param currentdir
	 * @param zos
	 * @param messageUtilisateur
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @see http://www.developpez.net/forums/viewtopic.php?p=1724764
	 */
	private static void zipDirDoubleProgressBar(final String basedir,
			final String currentdir, final ZipOutputStream zos,
			final int NbFIchierAZipper, final JProgressBar ProgressTotale,
			final JProgressBar Progress, final JLabel messageUtilisateur)
			throws FileNotFoundException, IOException {

		// create a new File object based on the directory we have to zip
		final File zipDir = (currentdir != null) ? new File(basedir, currentdir)
				: new File(basedir);
		// get a listing of the directory content
		final String[] dirList = zipDir.list();
		final byte[] readBuffer = new byte[BUFFER_SIZE];
		int bytesIn = 0;
		// On met pas File.separator, mais "/" parce que c'est le caract�re
		// utilis�
		// dans les ZIP.
		final String currentdir2 = (currentdir != null) ? (currentdir + "/")
				: "";

		File f;
		FileInputStream fis;
		BufferedInputStream bis;
		ZipEntry anEntry;
		// Create an empty entry with just dir name, like WinZip, so unzipToDir
		// will
		// behave correctly.
		if (currentdir2.length() > 0) {
			anEntry = new ZipEntry(ParseString.removeAccent(currentdir2));
			zos.putNextEntry(anEntry);
			zos.closeEntry();
		}
		// loop through dirList, and zip the files
		for (int i = 0; i < dirList.length; i++) {
			f = new File(zipDir, dirList[i]);
			if (!f.exists()) {
				continue;
			}
			if (f.isDirectory()) {
				// if the File object is a directory, call this
				// function again to add its content recursively
				zipDirDoubleProgressBar(basedir, currentdir2 + dirList[i], zos,
						NbFIchierAZipper, ProgressTotale, Progress,
						messageUtilisateur);
				continue;
			}
			// if we reached here, the File object f was not a directory
			// create a FileInputStream on top of f
			fis = new FileInputStream(f);
			try {
				bis = new BufferedInputStream(fis, BUFFER_SIZE);
				try {
					// create a new zip entry
					anEntry = new ZipEntry(ParseString.removeAccent(currentdir2
							+ dirList[i]));
					anEntry.setTime(f.lastModified());

					// place the zip entry in the ZipOutputStream object
					zos.putNextEntry(anEntry);
					long tailleDejaTrait�e = 0;
					// now write the content of the file to the ZipOutputStream
					long progressionEnCours = 0;
					NbFIchierDejaZipp� = NbFIchierDejaZipp� + 1;

					final long ProgressionTotale = NbFIchierDejaZipp� * 100
							/ NbFIchierAZipper;

					final int ProgressionR�elle = (int) (ProgressionTotale);

					ProgressTotale.setString("Total : " + ProgressionR�elle
							+ "%");
					ProgressTotale.setValue(ProgressionR�elle);
					messageUtilisateur.setText("Compression de "
							+ NbFIchierDejaZipp� + " fichier(s)  / sur "
							+ NbFIchierAZipper + " au total");
					// messageUtilisateur.setText("Compression du fichier : " +
					// f.getName());
					while ((bytesIn = bis.read(readBuffer, 0, BUFFER_SIZE)) != -1) {
						tailleDejaTrait�e = tailleDejaTrait�e + bytesIn;
						zos.write(readBuffer, 0, bytesIn);
						progressionEnCours = (100 * tailleDejaTrait�e)
								/ f.length();
						// Progress.setString (progressionEnCours + " %");
						Progress.setString(f.getName() + " : "
								+ progressionEnCours + " %");
						final int progress = (int) progressionEnCours;
						Progress.setValue(progress);
					}
					zos.closeEntry();
				} finally {
					bis.close();
				}
			} finally {
				// close the Stream
				fis.close();
			}
		}
	}

	public static boolean zipDir(final String EmplacementaArchiver,
			final String destination) throws FileNotFoundException, IOException {
		// Cr�ation d'un flux d'�criture vers un fichier
		final FileOutputStream fos = new FileOutputStream(destination);
		try {
			// Ajout du checksum : Adler32 (plus rapide) ou CRC32
			final CheckedOutputStream checksum = new CheckedOutputStream(fos,
					new Adler32());
			try {
				// Cr�ation d'un buffer de sortie afin d'am�liorer les
				// performances
				final BufferedOutputStream bos = new BufferedOutputStream(
						checksum, BUFFER_SIZE);
				try {
					// Cr�ation d'un flux d'�criture Zip vers le fichier �
					// travers le buffer
					final ZipOutputStream zos = new ZipOutputStream(bos);
					try {
						// Compression maximale
						try {
							zos.setMethod(ZipOutputStream.DEFLATED);
							zos.setLevel(Deflater.BEST_COMPRESSION);
						} catch (final Exception ignor) {
							System.out.println(ignor);
							Historique
									.ecrire("Erreur lors de l'archivage du fichier : "
											+ EmplacementaArchiver
											+ " vers le chemin : "
											+ destination);
							Historique
									.ecrire("Message de l'api zip : " + ignor);
							return false;
						}
						zipDir(EmplacementaArchiver, null, zos);
					} finally {
						zos.close();
					}
				} finally {
					bos.close();
				}
			} finally {
				checksum.close();
			}
		} finally {
			fos.close();
		}
		return true;
	}

	private static void zipDir(final String emplacementaArchiver,
			final String currentdir, final ZipOutputStream zos)
			throws IOException {
		// create a new File object based on the directory we have to zip
		final File zipDir = (currentdir != null) ? new File(
				emplacementaArchiver, currentdir) : new File(
				emplacementaArchiver);
		// get a listing of the directory content
		final String[] dirList = zipDir.list();
		final byte[] readBuffer = new byte[BUFFER_SIZE];
		int bytesIn = 0;
		// On met pas File.separator, mais "/" parce que c'est le caract�re
		// utilis�
		// dans les ZIP.
		final String currentdir2 = (currentdir != null) ? (currentdir + "/")
				: "";

		File f;
		FileInputStream fis;
		BufferedInputStream bis;
		ZipEntry anEntry;
		// Create an empty entry with just dir name, like WinZip, so unzipToDir
		// will
		// behave correctly.
		if (currentdir2.length() > 0) {
			anEntry = new ZipEntry(ParseString.removeAccent(currentdir2));
			zos.putNextEntry(anEntry);
			zos.closeEntry();
		}
		// loop through dirList, and zip the files
		for (int i = 0; i < dirList.length; i++) {
			f = new File(zipDir, dirList[i]);
			if (!f.exists()) {
				continue;
			}
			if (f.isDirectory()) {
				// if the File object is a directory, call this
				// function again to add its content recursively
				zipDir(emplacementaArchiver, currentdir2 + dirList[i], zos);
				continue;
			}
			// if we reached here, the File object f was not a directory
			// create a FileInputStream on top of f
			fis = new FileInputStream(f);
			try {
				bis = new BufferedInputStream(fis, BUFFER_SIZE);
				try {
					// create a new zip entry
					anEntry = new ZipEntry(ParseString.removeAccent(currentdir2
							+ dirList[i]));
					anEntry.setTime(f.lastModified());

					// place the zip entry in the ZipOutputStream object
					zos.putNextEntry(anEntry);
					long tailleDejaTrait�e = 0;
					// now write the content of the file to the ZipOutputStream
					// long progressionEnCours=0;
					// messageUtilisateur.setText("Compression du fichier : " +
					// f.getName());
					while ((bytesIn = bis.read(readBuffer, 0, BUFFER_SIZE)) != -1) {
						tailleDejaTrait�e = tailleDejaTrait�e + bytesIn;
						zos.write(readBuffer, 0, bytesIn);
						// progressionEnCours =
						// (100*tailleDejaTrait�e)/f.length();
						// Progress.setString (progressionEnCours + " %");
						// int progress=(int) progressionEnCours;
						// Progress.setValue(progress);
					}
					zos.closeEntry();
				} finally {
					bis.close();
				}
			} finally {
				// close the Stream
				fis.close();
			}
		}

	}
}
