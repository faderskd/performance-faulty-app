# performance-faulty-app

STD 11.05.2023

sync; echo 3 | sudo tee /proc/sys/vm/drop_caches
sudo vmtouch /playground/log.txt
curl -X GET http://localhost:8080/events/1003