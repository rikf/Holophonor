Holophonor
==========

![Holophonor](https://raw.github.com/rikf/Holophonor/master/Holophonor.jpg)

[![Build Status](https://travis-ci.org/rikf/Holophonor.png?branch=master)](https://travis-ci.org/rikf/Holophonor)

The Holophonor is a musical instrument of the 30th Century, it is best described as a combination of an Oboe and a Holographic Projector.

The Holophonor is alaso a light weight instrumentation tool for the jvm.


Holophonor is designed to provide a very light weight, low overhead (on the order of tens of nano seconds on my 13" rMBP) and robust tool for profiling the JVM in any environment from development to production. It does this by instrumenting bytecode at runtime using a combination of [ASM](http://asm.ow2.org/) and JVM [Agents](http://docs.oracle.com/javase/6/docs/api/java/lang/instrument/package-summary.html)

Holophonor's only external runtime dependency is **scala-library**. ASM is rerooted in this source tree to prevent conflicts with other libraries that may be using asm.

Holophonor provides two metrics for every method call that it instruments.

1. Elapsed time using [System.nanoTime()](http://docs.oracle.com/javase/7/docs/api/java/lang/System.html#nanoTime(\)) 
2. Cpu time using [ThreadMXBean.getCurrentThreadCpuTime()](http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/management/ThreadMXBean.html#getCurrentThreadCpuTime(\))

Both of these methods are called at the start and end of every method invocation.

This version of Holophonor comes with only one *StatsCollector* that is a *PrintingStatsCollector* that simply prints out the timing for each instrumented method call. However is it trivial to implement additional Collectors

###What could this be used for?

Some ideas are.

1. A dead code finder. Implement a stats collector that records all method calls. Deploy it to a production or UAT environment and compare the recorded calls with the methods in your jar/source tree.
2. Production monitoring of application performance. A lightweight alternative to some of the more enterprie application performance monitoring solutions.
3. General instrumentation of business methods to provide low level insight into how users are using a paticular application.


###How to use?
Either checkout this repository and build using 
```bash
sbt assembly
```
Or download [holophonor-0.1.jar](https://github.com/rikf/Holophonor/raw/master/holophonor-assembly-0.1.jar)

Then add the following flag to your java process.

``` -javaagent:/Path/to/holophonor/holophonor-assembly-0.1.jar ```

The output for a method call will look something like this

```
Method Inovation
Full qualified method name: the.next/big/thing/for/pets/Owner#getPets()Ljava/util/List;
Wall Time: 230000
Cpu Time: 225000
```

Note the timings are in nano seconds so divide by 1000000 to get milliseconds

Have fun :)

## License
Holophonor is released under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).

###ASM License

This program uses the ASM bytecode engineering library which is distributed
with the following notice:

Copyright (c) 2000-2005 INRIA, France Telecom
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.

3. Neither the name of the copyright holders nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE


ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
THE POSSIBILITY OF SUCH DAMAGE.

###Image of Holophonor
Fair Use - this image is copyrighted, but used here under [Wikipedia's fair use](http://en.wikipedia.org/wiki/Fair_use) guidelines
Owner/Creator The Twentieth Century Fox Film Corporation and its various divisions
