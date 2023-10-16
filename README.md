# Spis treści
1. Wprowadzenie - opis działani aplikacji
2. Opis zadania
   1. Jak działa benchmark
   2. Jak uruchomić benchmark samodzielnie
   3. **Jaki jest Twój cel**

# Wprowadzenie 
Aplikacja którą widzisz pozwala na zapisywanie i odczytywanie eventów

### Posiada proste api:
1. Zapisywanie Eventów
```shell
curl -X POST http://localhost:8080/events -H 'Content-Type: application/json' -d '{"content": "Hello World!"}'
{"offset":0}
```
2. Odczytywanie eventów:

```shell
curl http://localhost:52041/events/1
{"content":"hello world"}
```

### założenia aplikacji
- Eventy wysyłane do aplikacji są rzeczywiście zapisywane w pliku log.txt, nie są tylko przechowywane w pamięci

# Opis zadania

### Jak działa benchmark
Celem tego akapitu jest przybliżenie działania benchmarku. I tylko benchmark nie może być modyfikowany.

---
Przeprowadziliśmy benchmark intefrace EventLog za pomocą jmh, którego celem jest zweryfikowanie ilości operacji na milisekundę (ops/ms).

Implementację benchmarku możesz sprawdzić w klasie SimpleEventLogBenchmark,
natomiast jego konfiguracja znajduje się w `build.gradle`.

Benchmark przygotowuje się poprzez wypełnienie pliku `log.txt` przkładowymi eventami. Natomiast nie jest to uwzględniane w wynikach.
To co jest mierzone, to ilość wykonanych odczytów z `EventLog`:
```java
public String readFromLog(EventLogState state) {
    return eventLog.get(state.key++ % state.numberOfStoredEvents).content();
}
```

Benchmark uruchamia 3 iteracje w ramach rozgrzania się, a następnie 5 iteracji właściwych mierząc wyniki.

Jego przykładowy przebieg wygląda następująco:
```shell
# Warmup: 3 iterations, 10 s each
# Measurement: 5 iterations, 10 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: pl.allegro.tech.eden.performancefaultyapp.SimpleEventLogBenchmark.readFromLog

# Run progress: 0.00% complete, ETA 00:01:20
# Fork: 1 of 1
# Warmup Iteration   1: 598.810 ops/ms
# Warmup Iteration   2: 603.845 ops/ms
# Warmup Iteration   3: 597.754 ops/ms
Iteration   1: 605.271 ops/ms [39s]
Iteration   2: 602.975 ops/ms [49s]
Iteration   3: 605.938 ops/ms [1m]
Iteration   4: 607.089 ops/ms [1m 10s]
Iteration   5: 602.959 ops/ms [1m 21s]


Result "pl.allegro.tech.eden.performancefaultyapp.SimpleEventLogBenchmark.readFromLog":
  604.846 ±(99.9%) 7.065 ops/ms [Average]
  (min, avg, max) = (602.959, 604.846, 607.089), stdev = 1.835
  CI (99.9%): [597.782, 611.911] (assumes normal distribution)


# Run complete. Total time: 00:01:24
```

## uruchomienie benchmarku
Akapit opisuje jak samodzielnie uruchomić benchmark

1. W `application.properties` wskaż pod jaką ścieżką będzie znajdował się plik przechowujący eventy na VM. Może to być np. `event-log.logFilePath=/home/QXLTECH/user/log.txt`
2. Zbuduj aplikację po przez jej uruchomienie na `std-red.hermes-dev.pl-kra-4.dc4.local`
3. Upenij się, że aplikacja uruchomiłą się i znajduje się na VM
4. przejdź do katalogu z aplikacją na VM maszynie i uruchom benchmark:
```shell
./gradlew clean jmh
```
--- 
# Twój cel!
Okazuje się, że wyniki EventLog mieszone przez benchmark można znacznie poprawić.

Nam udało się uzyskać następujące wyniki:
```shell
# Warmup Iteration   1: 14310.387 ops/ms
# Warmup Iteration   2: 14097.837 ops/ms
# Warmup Iteration   3: 14314.290 ops/ms

Iteration   1: 14315.816 ops/ms
Iteration   2: 14295.157 ops/ms
Iteration   3: 14327.585 ops/ms
Iteration   4: 14337.921 ops/ms
Iteration   5: 14216.340 ops/ms
```

Twoim zadaniem jest uzyskanie podobnych rezultatów jak my (+/- 14k ops/ms) oraz wyjaśnienie z czego wynika poprawa.

Powodzenia!