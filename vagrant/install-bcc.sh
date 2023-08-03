#!/bin/bash

wget https://github.com/iovisor/bcc/releases/download/v0.28.0/bcc-src-with-submodule.tar.gz
tar xf bcc-src-with-submodule.tar.gz
cd bcc/
apt install -y python-is-python3
apt install -y \
  zip \
  bison \
  build-essential \
  cmake \
  flex \
  git \
  libedit-dev \
  libllvm11 \
  llvm-11-dev \
  libclang-11-dev \
  zlib1g-dev \
  libelf-dev \
  libfl-dev \
  python3-distutils
apt install -y checkinstall
mkdir build
cd build/
cmake -DCMAKE_INSTALL_PREFIX=/usr -DPYTHON_CMD=python3 ..
make
checkinstall
