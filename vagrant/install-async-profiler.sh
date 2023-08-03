#!/bin/bash

ASYNC_PROFILER_VERSION=2.9

sysctl kernel.perf_event_paranoid=1
sysctl kernel.kptr_restrict=0

cd /tmp

curl -L "https://github.com/jvm-profiling-tools/async-profiler/releases/download/v$ASYNC_PROFILER_VERSION/async-profiler-$ASYNC_PROFILER_VERSION-linux-x64.tar.gz" | tar -xvz

mv "async-profiler-$ASYNC_PROFILER_VERSION-linux-x64" /usr/share/async-profiler
