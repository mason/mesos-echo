package com.masonchan.mesos;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.mesos.Executor;
import org.apache.mesos.ExecutorDriver;
import org.apache.mesos.MesosExecutorDriver;
import org.apache.mesos.Protos;

public class EchoExecutor implements Executor{

	private final static Logger LOGGER = Logger.getLogger(EchoExecutor.class);

	public void registered(ExecutorDriver executorDriver, Protos.ExecutorInfo executorInfo, Protos.FrameworkInfo frameworkInfo, Protos.SlaveInfo slaveInfo) {

	}

	public void reregistered(ExecutorDriver executorDriver, Protos.SlaveInfo slaveInfo) {

	}

	public void disconnected(ExecutorDriver executorDriver) {

	}

	public void launchTask(ExecutorDriver executorDriver, Protos.TaskInfo taskInfo) {
		// It's important to mark task statuses to allow the framework to know it can free up the offered resources for other tasks

		LOGGER.info("Launching Task " + taskInfo.getName());
		executorDriver.sendStatusUpdate(Protos.TaskStatus.newBuilder().setTaskId(taskInfo.getTaskId()).setState(Protos.TaskState.TASK_RUNNING).build());
		LOGGER.info("Hello World");
		executorDriver.sendStatusUpdate(Protos.TaskStatus.newBuilder().setTaskId(taskInfo.getTaskId()).setState(Protos.TaskState.TASK_FINISHED).build());
		LOGGER.info("Finished Task " + taskInfo.getName());
	}

	public void killTask(ExecutorDriver executorDriver, Protos.TaskID taskID) {

	}

	public void frameworkMessage(ExecutorDriver executorDriver, byte[] bytes) {

	}

	public void shutdown(ExecutorDriver executorDriver) {

	}

	public void error(ExecutorDriver executorDriver, String s) {

	}

	public static void main(String[] args) {
		BasicConfigurator.configure(); // log4j setup
		LOGGER.info("Starting Executer");

		MesosExecutorDriver driver = new MesosExecutorDriver(new EchoExecutor());
		System.exit(driver.run() == Protos.Status.DRIVER_STOPPED ? 0 : 1);
	}
}
