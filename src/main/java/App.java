public class App {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Run: java App <filename.txt>");
            System.exit(0);
        }
        Cipher cipher = new Cipher(20);
        String message = FileUtils.readFile(args[0]);

        System.out.println("Tentando descriptografar texto em portugues:");
        System.out.println("####################################################");
        var decryptedMessagePor = cipher.decrypt(message, "portuguese");
        System.out.println("####################################################\n");

        System.out.println("Tentando descriptografar texto em ingles:");
        System.out.println("####################################################");
        var decryptedMessageEng = cipher.decrypt(message, "english");
        System.out.println("####################################################");

        FileUtils.writeFile("./src/main/java/texts/decrypted/decrypted-por.txt", decryptedMessagePor);
        FileUtils.writeFile("texts.decrypted-eng.txt", decryptedMessageEng);
    }

}
