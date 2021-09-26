import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cipher {

    private final static double ciPor = 0.072723;
    private final static double ciEng = 0.065;
    private final int maxKeySize;
    private double ciLanguage;
    private List<Character> mostFrequentLetters;
    private final List<Character> mostFrequentLettersPor = Arrays.asList('a', 'e', 'o');
    private final List<Character> mostFrequentLettersEng = Arrays.asList('e', 't', 'a');

    public Cipher(int maxKeySize) {
        this.maxKeySize = maxKeySize;
    }

    public String decrypt(String message, String language) {
        ciLanguage = language.equals("english") ? ciEng : ciPor;
        mostFrequentLetters = language.equals("english") ? mostFrequentLettersEng : mostFrequentLettersPor;
        var keySize = this.getKeySize(message);
        var key = this.getKey(message, keySize);
        var decryptedMessage = this.decryptMessage(keySize, key, message);
        System.out.println("Tamanho da chave: " + keySize);
        System.out.println("Chave: " + key);
        System.out.println("Mensagem descriptografada: " + decryptedMessage.substring(0, 100));
        return decryptedMessage;
    }

    public int getKeySize(String encryptedMessage) {
        var cisAverage = new ArrayList<Double>();
        for (int i = 1; i <= maxKeySize; i++) {
            var cis = new ArrayList<Double>();
            for (int j = 0; j < i; j++) {
                var sb = new StringBuilder();
                for (int k = 0; k < encryptedMessage.length() - j; k += i) {
                    sb.append(encryptedMessage.charAt(k + j));
                }
                var ci = this.calculateCoincidenceIndex(sb.toString());
                cis.add(ci);
            }
            var average = cis.stream().mapToDouble(ci -> ci).sum() / i;
            cisAverage.add(average);
        }

        double range = 0.001;
        while (true) {
            for (int i = 0; i < cisAverage.size(); i++) {
                if (ciLanguage + range > cisAverage.get(i) && ciLanguage - range < cisAverage.get(i)) {
                    return i + 1;
                }
            }
            range += 0.001;
        }
    }

    public double calculateCoincidenceIndex(String encryptedMessage) {
        var freq = this.getFrequency(encryptedMessage);
        double textLength = encryptedMessage.length();
        double a = 1D / (textLength * (textLength - 1D));
        var ic = a * freq.values()
                .stream()
                .mapToDouble(x -> x.doubleValue() * (x.doubleValue() - 1))
                .reduce(Double::sum)
                .orElse(0D);
        ic = new BigDecimal(ic).setScale(4, RoundingMode.HALF_UP).doubleValue();
        return ic;
    }

    private Map<String, Long> getFrequency(String encryptedMessage) {
        return Stream.of(encryptedMessage.toLowerCase().split(""))
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()));
    }

    public String getKey(String encryptedMessage, int keySize) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < keySize; i++) {
            var sb = new StringBuilder();
            for (int k = 0; k < encryptedMessage.length() - keySize; k += keySize) {
                sb.append(encryptedMessage.charAt(i + k));
            }
            list.add(sb.toString());
        }

        StringBuilder sb = new StringBuilder();
        for (String line: list) {
            var freq = this.getFrequency(line);
            var sortedFreq = freq.entrySet().stream().sorted((o1, o2) -> o1.getValue().compareTo(o2.getValue())).collect(Collectors.toList());
            char dist = this.getBestKey(sortedFreq);
            sb.append(dist);
        }
        return sb.toString();
    }

    private char getBestKey(List<Map.Entry<String, Long>> sortedFreq) {
        var first = sortedFreq.get(sortedFreq.size()-1).getKey();
        var second = sortedFreq.get(sortedFreq.size()-2).getKey();
        var third = sortedFreq.get(sortedFreq.size()-3).getKey();

        char distFirst = this.getCharDistance(first.charAt(0), mostFrequentLetters.get(0));
        char distSecond = this.getCharDistance(second.charAt(0), mostFrequentLetters.get(1));
        char distThird = this.getCharDistance(third.charAt(0), mostFrequentLetters.get(2));

        if (distFirst == distSecond && distFirst == distThird) {
            return distFirst;
        } else {
            distFirst = getCharDistance(first.charAt(0), mostFrequentLetters.get(1));
            distSecond = getCharDistance(second.charAt(0), mostFrequentLetters.get(0));
            if (distFirst == distSecond && distFirst == distThird) {
                return distFirst;
            } else {
                distFirst = getCharDistance(first.charAt(0), mostFrequentLetters.get(0));
                return distFirst;
            }
        }
    }

    public char getCharDistance(int letter, int numFreq) {
        char c = (char) ((letter) - (numFreq - 'a'));
        return c < 'a' ? (char) (c + 26) : c;
    }

    public String decryptMessage(int m, String key, String message) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < message.length(); i += m) {
            for (int j = 0; j < m; j++) {
                if (i + j >= message.length()) {
                    return text.toString();
                }
                char c = (char) (message.charAt(i + j) - (key.charAt(j) - 'a'));
                if (c < 'a') {
                    c += 26;
                }
                text.append(c);
            }
        }
        return text.toString();
    }

}
