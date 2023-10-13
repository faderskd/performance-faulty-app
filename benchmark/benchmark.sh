wrk -t1 -c1 -d600s -R200  http://localhost:8000/events -s ./producer.lua -L

#sleep 3
#
#wrk2 -t1 -c1 -d60s -R100  http://localhost:8082/ -s ./consumer.lua -L