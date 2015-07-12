# About
A mesos framework that prints Hello World from the mesos slave instance to standard out

## Compile Mesos
http://mesos.apache.org/gettingstarted/

## Start Mesos
* Start master: `./bin/mesos-master.sh --ip=127.0.0.1 --work_dir=/var/lib/mesos`
* Start slave: `./bin/mesos-slave.sh --master=127.0.0.1:5050`
* You should be able to see this page: `http://127.0.0.1:5050`


## Compile
```
  mvn clean package
```

## Run
If you compiled mesos from source as described in `Compiling Mesos`, then you will have a .libs directory. This is directory that holds native code that mesos needs to operate.
```
  java -Djava.library.path=<Path to compiled mesos>/mesos-0.22.1/build/src/.libs/ -cp target/uber-mesos-echo-1.0-SNAPSHOT.jar com.masonchan.mesos.EchoScheduler 127.0.0.1:5050
```

## Look at Standard Out to Verify it Worked
Standard out should be stored in a data directory. By default it is in `/tmp/mesos/`. To see the standard out of the slave with id `20150712-022642-16777343-5050-24271-S0` and framework id `0150712-022642-16777343-5050-24271-0028` it would be located here: 

```
  vi /tmp/mesos/slaves/20150712-022642-16777343-5050-24271-S0/frameworks/20150712-022642-16777343-5050-24271-0028/executors/default/runs/latest/stdout
```

## Mesos Framework Concepts
A Mesos framework is composed of three important parts

1. **Driver**: The driver is the concrete class that gets run to kick off the scheduler or executor. This class isn't serialized through protobuf.
2. **Scheduler**: The scheduler accepts resource (mem, cpu, ports, etc) offers and schedules executors to run on those resources. A scheduler implementation is also used as a callback triggered from the scheduler driver.
3. **Executor**: The executor executes a command defined from the scheduler through /bin/sh -c. An executor implementation is also used as a callback triggered from the executor driver.

## The Mesos Workflow
A MesosSchedulerDriver is instantiated and is passed a concrete implementation of the mesos Scheduler interface.
Throughout the execution of the MesosSchedulerDriver, it will call various methods defined in your concrete class as hooks to the mesos workflow.
The most important method in your scheduler implementation is `resourceOffers(SchedulerDriver schedulerDriver, List<Protos.Offer> list)`.
This method is called by the driver and provides a list of offers that the scheduler should then decide whether to use to run a job.
If the resources are satisfactory, you will need to create a Task to be run by the driver. The most important aspect of a task is the ExecutorInfo property.
This is a protobuf object that is serialized client side by the scheduler driver to be passed to a mesos master via a task. The master will then run the task on a slave instance.
For example lets say we define a task with the following ExecutorInfo property: `Protos.ExecutorInfo.newBuilder().setCommand("echo mesos is cool")`.
The mesos framework will pass this task from the scheduler to a slave which will execute the ExecutorInfo command with `/bin/sh -c`.
This will cause the slave to print `mesos is cool` in standard out. You can now imagine a more advanced scenario where the ExecutorInfo command runs a jar.
This project does just that. It runs a jar that has a main method that runs a custom executor on a slave.
