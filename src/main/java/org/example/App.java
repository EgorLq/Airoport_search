package org.example;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class App  {
    // получаем из списка числа
    static final Function<String[], Function<Integer, String>> getFromList = list -> number -> list[number];
    // убираем все ковычки
    static final UnaryOperator<String> replaceAllSign = stringForTransformation -> stringForTransformation.replaceFirst("\"", "");
    // подготавливаемся к перечитыванию
    static final Function<PreparedForSearch, Function<String, Boolean>> containsSting = preparedForSearch -> predicateString ->
            preparedForSearch.getStringCondition().toLowerCase().startsWith(predicateString.toLowerCase());
    // что бы столбцы начинались с единицы

    static final UnaryOperator<Integer> columnOffsetMinus = integer -> integer - 1;
    // что бы кол во столбцов было на 1 больше
    static final UnaryOperator<Integer> lineSizeTransferPlus = integer -> integer + 1;
    static final List<String> warmUpList = Stream.of("Bo", "A", "C", "D", "E", "A").collect(Collectors.toList());
    // разделяет стобцы по запятым
    private static final String DELIMITER = ",";

    // мейн класс
    public static void main(String[] args) throws IOException {
        // находим файл
        File file = new File("airports.csv");

        List<PreparedForSearch> preparedForSearches = readFileForCache(Integer.valueOf(args[0]), file);

        //делаю прогрев jvm
        warmUpList.forEach(warmObject -> {
            Stream<PreparedForSearch> warmingUpFilterA = filteredForSearch(preparedForSearches, warmObject);
            warmingUp(file, warmingUpFilterA);
        });
        // инициализируем сканер
        Scanner in = new Scanner(System.in);
        String searchFor;
        // делаем цикл в котором можем совершать повторный поиск
        do {
            System.out.print("Введите предикат для поиска или введите \"!quit\" для выхода ");

            searchFor = in.next();

            long start = System.currentTimeMillis();

            Stream<PreparedForSearch> filteredForSearch = filteredForSearch(preparedForSearches, searchFor);
            AtomicInteger count = printAndCountResultList(file, filteredForSearch);

            if(!searchFor.equals("!quit")) {
                System.out.println("List size = " + count);
                System.out.println((double) (System.currentTimeMillis() - start));
            }
        } while(!searchFor.equals("!quit"));
    }
    //выводим  и подсчитывает результаты
    private static AtomicInteger printAndCountResultList(File file, Stream<PreparedForSearch> filteredForSearch) {
        AtomicInteger count = new AtomicInteger(0);

        filteredForSearch
                .map(searchResult -> appendString(readFile(searchResult, file), searchResult))
                .forEach(resultSearchObject -> {
                    System.out.println(resultSearchObject);
                    count.incrementAndGet();
                });

        return count;
    }
    // разогрев jvm
    private static void warmingUp(File file, Stream<PreparedForSearch> filteredForSearch) {
        AtomicInteger count = new AtomicInteger(0);

        filteredForSearch
                .parallel()
                .map(searchResult -> appendString(readFile(searchResult, file), searchResult))
                .forEach(resultSearchObject -> count.incrementAndGet());
    }
    //предварительно сортирую
    private static Stream<PreparedForSearch> filteredForSearch(List<PreparedForSearch> preparedForSearches, final String searchFor) {
        return preparedForSearches
                .stream()
                .filter(filterForSearch -> containsSting.apply(filterForSearch).apply(searchFor))
                .sorted();
    }

    private static String appendString(String result, PreparedForSearch searchResult) {
        StringBuilder stringBuilder = new StringBuilder();
        if (searchResult.getStringCondition().lastIndexOf("\"") != -1) {
            stringBuilder.append("\"");
        }
        return stringBuilder.append(searchResult.getStringCondition()).append("[")
                .append(result).append("]").toString();
    }
    //читаю из кеша
    private static List<PreparedForSearch> readFileForCache(final Integer column, final File path) {
        try (Scanner reader = new Scanner(path)) {
            List<PreparedForSearch> lst = new ArrayList<>();
            while (reader.hasNextLine()) {

                String line = reader.nextLine();

                String variable = replaceAllSign
                        .apply(getFromList.apply(line.split(DELIMITER))
                                .apply(columnOffsetMinus.apply(column)));

                Integer reduce = lst.stream()
                        .map(PreparedForSearch::getLineByteSize)
                        .reduce(0, Integer::sum);

                Integer lineByteSize = lineSizeTransferPlus
                        .apply(line.getBytes(StandardCharsets.UTF_8).length);

                lst.add(new PreparedForSearch(variable, reduce, lineByteSize));
            }
            return lst;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    //читаю файл
    private static String readFile(PreparedForSearch preparedForSearches, final File path) {
        try (RandomAccessFile reader = new RandomAccessFile(path, "r")) {
            byte[] bytes = new byte[preparedForSearches.getLineByteSize()];
            reader.seek(preparedForSearches.getByteSum());
            reader.read(bytes);
            return new String(bytes).trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
