package smtp;

/*
 * Some SMTP servers require a username and password authentication before you
 * can use their Server for Sending mail. This is most common with couple of
 * ISP's who provide SMTP Address to Send Mail. This Program gives any example
 * on how to do SMTP Authentication (User and Password verification) This is a
 * free source code and is provided as it is without any warranties and it can
 * be used in any your code for free. Author : Sudhir Ancha
 */

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import Utilitaires.Historique;

/*
 * To use this program, change values for the following three constants,
 * SMTP_HOST_NAME -- Has your SMTP Host Name SMTP_AUTH_USER -- Has your SMTP
 * Authentication UserName SMTP_AUTH_PWD -- Has your SMTP Authentication
 * Password Next change values for fields emailMsgTxt -- Message Text for the
 * Email emailSubjectTxt -- Subject for email emailFromAddress -- Email Address
 * whose name will appears as "from" address Next change value for "emailList".
 * This String array has List of all Email Addresses to Email Email needs to be
 * sent to. Next to run the program, execute it as follows,
 * SendMailUsingAuthentication authProg = new SendMailUsingAuthentication();
 */

public class SendMailUsingAuthentication {

	private static final String SMTP_HOST_NAME = "smtp.laposte.net";
	private static final String SMTP_AUTH_USER = "carnet.client";
	private static final String SMTP_AUTH_PWD = "gouranga08";
	private static final String FROM = "carnet.client@laposte.net";

	private static final String emailMsgTxt = "Online Order Confirmation Message. Also include the Tracking Number.";
	private static final String emailSubjectTxt = "Order Confirmation Subject";
	private static final String emailFromAddress = "equinox.simon@laposte.net";

	// Add List of Email address to who email needs to be sent to
	private static final String[] emailList = { "s.mardine@gmail.com" };

	public static void main(final String args[]) throws Exception {
		final SendMailUsingAuthentication smtpMailSender = new SendMailUsingAuthentication();
		smtpMailSender.postMail(emailList, emailSubjectTxt, emailMsgTxt,
				emailFromAddress);

		System.out.println("Sucessfully Sent mail to All Users");
	}

	public void postMail(final String recipients[], final String subject,
			final String message, final String from) throws MessagingException {
		final boolean debug = false;

		// Set the host smtp address
		final Properties props = new Properties();
		props.put("mail.smtp.host", SMTP_HOST_NAME);
		props.put("mail.smtp.auth", "true");

		final Authenticator auth = new SMTPAuthenticator();
		final Session session = Session.getDefaultInstance(props, auth);

		session.setDebug(debug);

		// create a message
		final Message msg = new MimeMessage(session);

		// set the from and to address
		final InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);

		final InternetAddress[] addressTo = new InternetAddress[recipients.length];
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i]);
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Setting the Subject and Content Type
		msg.setSubject(subject);
		msg.setContent(message, "text/plain");
		Transport.send(msg);
	}

	/**
	 * SimpleAuthenticator is used to do simple authentication when the SMTP
	 * server requires it.
	 */
	private class SMTPAuthenticator extends javax.mail.Authenticator {

		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			final String username = SMTP_AUTH_USER;
			final String password = SMTP_AUTH_PWD;
			return new PasswordAuthentication(username, password);
		}
	}

	public boolean postMail(final String subject, final String message) {
		final boolean debug = false;

		// Set the host smtp address
		final Properties props = new Properties();
		props.put("mail.smtp.host", SMTP_HOST_NAME);
		props.put("mail.smtp.auth", "true");

		final Authenticator auth = new SMTPAuthenticator();
		final Session session = Session.getDefaultInstance(props, auth);

		session.setDebug(debug);

		// create a message
		final Message msg = new MimeMessage(session);

		// set the from and to address
		InternetAddress addressFrom = null;
		try {
			addressFrom = new InternetAddress(FROM);
		} catch (final AddressException e1) {

			Utilitaires.Historique.ecrire("Message d'erreur: " + e1);
			return false;
		}
		try {
			msg.setFrom(addressFrom);
		} catch (final MessagingException e) {

			Historique.ecrire("Erreur lors de l'envoi du mail :" + e);

			return false;
		}

		final InternetAddress[] addressTo = new InternetAddress[emailList.length];
		for (int i = 0; i < emailList.length; i++) {
			try {
				addressTo[i] = new InternetAddress(emailList[i]);
			} catch (final AddressException e) {

				Historique.ecrire("Erreur lors de l'envoi du mail :" + e);

				return false;
			}
		}
		try {
			msg.setRecipients(Message.RecipientType.TO, addressTo);
		} catch (final MessagingException e) {

			Historique.ecrire("Erreur lors de l'envoi du mail :" + e);

			return false;
		}

		// Setting the Subject and Content Type
		try {
			msg.setSubject(subject);
		} catch (final MessagingException e) {

			Historique.ecrire("Erreur lors de l'envoi du mail :" + e);

			return false;
		}
		try {
			msg.setContent(message, "text/plain");
		} catch (final MessagingException e) {

			Historique.ecrire("Erreur lors de l'envoi du mail :" + e);

			return false;
		}
		try {
			Transport.send(msg);
		} catch (final MessagingException e) {

			Historique.ecrire("Erreur lors de l'envoi du mail :" + e);

			return false;
		}
		return true;

	}

}
