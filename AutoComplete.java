import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class AutoComplete {
    public static void main(String[] args) {
        String filePath = "your file path here";
        String fileContent = readFile(filePath);
        fileContent = fileContent.toLowerCase();
        String[] allWordsFromContent = processContent(fileContent);
        Set<String> vocabularyFromContent = new HashSet<>();
        for (String aWord : allWordsFromContent) {
            vocabularyFromContent.add(aWord);
        }

        Map<String, Integer> getOneGramMap = buildOneGram(allWordsFromContent);
        Map<String, Integer> getTwoGramMap = buildTwoGram(allWordsFromContent);
        Map<String, Integer> getThreeGramMap = buildThreeGram(allWordsFromContent);
        int totalWords = allWordsFromContent.length;
//        printMap(getOneGramMap);
//        printMap(getTwoGramMap);
//        printMap(getThreeGramMap);
//        for (int i = 0; i < allWordsFromContent.length; i++) {
//            System.out.println(allWordsFromContent[i]);
//        }
        Map<String, Float> oneGramFrequencyMap = buildOneGramFrequency(getOneGramMap, totalWords);
        Map<String, Float> twoGramFrequencyMap = buildTwoGramFrequency(getTwoGramMap, getOneGramMap);
        Map<String, Float> threeGramFrequencyMap = buildThreeGramFrequency(getThreeGramMap, getTwoGramMap);
//        printMap(oneGramFrequencyMap);
//        printMap(twoGramFrequencyMap);
//        printMap(threeGramFrequencyMap);
        Scanner keyedinput = new Scanner(System.in);


        for (int i = 0; i < 1000; i++) {
            System.out.println("Please Enter A Sentence To Complete that has at least two allWordsFromContent");
            String sentence = keyedinput.nextLine();
            if (sentence.trim().equals("")) {
                continue;
            }
            String[] tokens = sentence.split(" ");
            if (tokens.length < 2) {
                System.out.println("Not enough allWordsFromContent");
                continue;
            }
            String lastTwoWords = tokens[tokens.length - 2] + " " + tokens[tokens.length - 1];
            Map<String, Double> finalMap = calculateFrequency(oneGramFrequencyMap, twoGramFrequencyMap, threeGramFrequencyMap, lastTwoWords, vocabularyFromContent);
            List<String> bestWords = getTopFrequencyWords(finalMap);
            bestWords.forEach(s -> System.out.println(s));
        }

    }


    public static String[] processContent(String fileContent) {
        // replace all punctuation with  space Period ?!,;:.
        //remove double quote and single quote and Brackets [](){}<>
        fileContent = fileContent.replaceAll("[?!,;:.]"," .");
        fileContent = fileContent.replaceAll("[\\[, \\], \\(, \\), \\{, \\}, \\<, \\>, \", ']" , " ");
        String[] processedText =  fileContent.split(" ");
        return processedText;
    }

    public static HashMap<String, Integer> buildOneGram(String[] allWords) {
        HashMap<String, Integer> oneGramMap = new HashMap<>();
        for (int i = 0; i < allWords.length; i++) {
            if (allWords[i].trim().length() == 0) {
                continue;
            }
            String key = allWords[i];
            if (oneGramMap.containsKey(allWords[i])) {
                oneGramMap.put(key, oneGramMap.get(key) + 1);
            }
            else {
                oneGramMap.put(key, 1);
            }
        }
        return oneGramMap;
    }

    public static HashMap<String, Integer> buildTwoGram(String[] allWords) {
        HashMap<String, Integer> twoGramMap = new HashMap<>();
        for (int i = 0; i < allWords.length - 1; i++) {
            if (allWords[i].trim().length() == 0) {
                continue;
            }
            if (allWords[i].trim().equals(".")) {
                continue;
            }
            String key = allWords[i] + " " + allWords[i + 1];
            if (twoGramMap.containsKey(key)) {
                twoGramMap.put(key, twoGramMap.get(key) + 1);
            }
            else {
                twoGramMap.put(key, 1);
            }
        }
        return twoGramMap;
    }

    public static HashMap<String, Integer> buildThreeGram(String[] allWords) {
        HashMap<String, Integer> threeGramMap = new HashMap<>();
        for (int i = 0; i < allWords.length - 2; i++) {
            if (allWords[i].trim().length() == 0) {
                continue;
            }
            if (allWords[i].trim().equals(".") || allWords[i+1].equals(".")) {
                continue;
            }
            String key = allWords[i] + " " + allWords[i + 1] + " " + allWords[i+2];
            if (threeGramMap.containsKey(key)) {
                threeGramMap.put(key, threeGramMap.get(key) + 1);
            }
            else {
                threeGramMap.put(key, 1);
            }
        }
        return threeGramMap;
    }

    public static void printMap(Map map) {
        map.entrySet().forEach(e -> System.out.println(e));
        System.out.println("--------------");
    }

    public static Map<String, Float> buildOneGramFrequency(Map<String, Integer> oneGramCount, int totalWords) {
        Map<String, Float> oneGramFrequency = new HashMap<>();
        // calculate one gram frequency
        for (String key : oneGramCount.keySet()) {
            int count = oneGramCount.get(key);
            oneGramFrequency.put(key, 1.0f * count / totalWords);
        }
        return oneGramFrequency;
    }

    public static Map<String, Float> buildTwoGramFrequency(Map<String, Integer> twoGramCount, Map<String, Integer> oneGramCount) {
        Map<String, Float> twoGramFrequency = new HashMap<>();
        //calculate two gram frequency
        for (String key : twoGramCount.keySet()) {
            int count = twoGramCount.get(key);
            String oneGram = key.split(" ")[0];
            twoGramFrequency.put(key, 1.0f * count / oneGramCount.get(oneGram));
        }
        return twoGramFrequency;
    }

    public static Map<String, Float> buildThreeGramFrequency(Map<String, Integer> threeGramCount, Map<String, Integer> twoGramCount) {
        Map<String, Float> threeGramFrequency = new HashMap<>();
        // calculate three gram frequency
        for (String key : threeGramCount.keySet()) {
            int count = threeGramCount.get(key);
            String[] words = key.split(" ");
            String twoGram = words[0] + " " + words[1];
            threeGramFrequency.put(key, 1.0f * count / twoGramCount.get(twoGram));
        }
        return threeGramFrequency;
    }

    public static double calculateFrequency(Map<String, Float> oneGramFrequency, Map<String, Float> twoGramFrequency,
                                           Map<String, Float> threeGramFrequency, String twoGramWord, String oneWord ) {
        double freq = 0.0f;

        float freq1 = 0.00001f;
        float freq2 = 0.00001f;
        float freq3 = 0.00001f;
        if (oneGramFrequency.containsKey(oneWord)) {
            freq1 = oneGramFrequency.get(oneWord);
        }

        String[] twoGramArray = twoGramWord.split("");
        String word2 = twoGramArray[1] + " " + oneWord;
        String word3 = twoGramWord + " " + oneWord;
        if (twoGramFrequency.containsKey(word2)) {
            freq2 = twoGramFrequency.get(word2);
        }
        if (threeGramFrequency.containsKey(word3)) {
            freq3 = threeGramFrequency.get(word3);
        }

        freq = Math.log(freq1) + Math.log(freq2) + Math.log(freq3);
        return freq;
    }


    public static Map<String, Double> calculateFrequency(Map<String, Float> oneGramFrequency, Map<String, Float> twoGramFrequency,
                                                        Map<String, Float> threeGramFrequency, String twoGramWord,
                                                        Set<String> vocabulary) {
        Map<String, Double> finalFrequency = new HashMap<>();
        for (String word : vocabulary) {
            double freq = calculateFrequency(oneGramFrequency, twoGramFrequency, threeGramFrequency, twoGramWord, word);
            finalFrequency.put(word, freq);
        }
        return finalFrequency;
    }

    public static List<String> getTopFrequencyWords(Map<String, Double> calculatedFrequency) {
        List<String> topFrequencyWord = new ArrayList<>();
        List<Map.Entry<String, Double>> entryList = calculatedFrequency.entrySet().stream().collect(Collectors.toList());
        Collections.sort(entryList, Map.Entry.comparingByValue());
        for (int i = entryList.size() - 1; i > entryList.size() - 5 - 1 && i > -1; i--) {
            topFrequencyWord.add(entryList.get(i).getKey());
        }
        return topFrequencyWord;
    }

    public static String readFile(String FileName) {
        String fileContent = " ";
        try {
            File myObj = new File(FileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                fileContent = fileContent + " " + line;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return fileContent;
    }
}
