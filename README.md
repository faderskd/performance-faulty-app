# performance-faulty-app

STD 11.05.2023

sync; echo 3 | sudo tee /proc/sys/vm/drop_caches
sudo vmtouch /playground/log.txt
curl -X GET http://localhost:8080/events/1003
sudo strace -ttT -fp 2858 2>&1 | grep -v -e "futex" -e "rusage" -e "ETIMEDOUT" -e "restart_syscall" -e "epoll_wait" | grep 3331