# performance-faulty-app

STD 11.05.2023

sync; echo 3 | sudo tee /proc/sys/vm/drop_caches
sudo vmtouch /playground/log.txt
curl -X GET http://localhost:8080/events/1003
sudo cgcreate -a $USER -t $USER -g memory,cpu:limitapp
echo 800M > /sys/fs/cgroup/limitapp/memory.high 

sudo cgexec -g memory:limitapp ./gradlew clean jmh -Pjmh.includes=.*ProduceConsume.*
