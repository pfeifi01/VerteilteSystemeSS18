#!/bin/bash
dd if=/dev/zero of=1KB.dat  bs=1K count=1;
dd if=/dev/zero of=10KB.dat  bs=10K  count=1;
dd if=/dev/zero of=100KB.dat  bs=100K  count=1;
dd if=/dev/zero of=1MB.dat  bs=1M  count=1;
dd if=/dev/zero of=10MB.dat  bs=10M  count=1;