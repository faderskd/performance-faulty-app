wrk2 -t1 -c1 -d60s -R100  http://localhost:8082/events -s ./producer.lua -L > producer.out &

sleep 3

wrk2 -t1 -c1 -d60s -R100  http://localhost:8082/ -s ./consumer.lua -L