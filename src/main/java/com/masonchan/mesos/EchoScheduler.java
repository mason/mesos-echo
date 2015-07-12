package com.masonchan.mesos;


import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.mesos.MesosSchedulerDriver;
import org.apache.mesos.Protos;
import org.apache.mesos.Scheduler;
import org.apache.mesos.SchedulerDriver;

public class EchoScheduler implements Scheduler {

	private final static Logger LOGGER = Logger.getLogger(EchoScheduler.class);

	EchoScheduler(){

	}

	public void registered(SchedulerDriver schedulerDriver, Protos.FrameworkID frameworkID, Protos.MasterInfo masterInfo) {

	}

	public void reregistered(SchedulerDriver schedulerDriver, Protos.MasterInfo masterInfo) {

	}

	public void resourceOffers(SchedulerDriver schedulerDriver, List<Protos.Offer> list) {
		LOGGER.info("Receiving Offers");
		for(Protos.Offer offer : list) {
			List<Protos.TaskInfo> taskInfos = new LinkedList<Protos.TaskInfo>();
			List<Protos.OfferID> offersIds = new LinkedList<Protos.OfferID>();
			Protos.TaskID taskId = Protos.TaskID.newBuilder().setValue(UUID.randomUUID().toString()).build();
			double offerCpus = 0;

			for (Protos.Resource resource : offer.getResourcesList()) {
				if (resource.getName().equals("cpus")) {
					offerCpus = resource.getScalar().getValue();
				}
			}

			if (offerCpus > 0) {
				String command = "";
				try {
					command = "java -cp " + new File(EchoScheduler.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()) + " com.masonchan.mesos.EchoExecutor";
				} catch (Exception e) {

				}
				Protos.ExecutorInfo executorInfo = Protos.ExecutorInfo.newBuilder().setExecutorId(Protos.ExecutorID.newBuilder().setValue("default")).setCommand(Protos.CommandInfo.newBuilder().setValue(command)).build();
				Protos.TaskInfo taskInfo = Protos.TaskInfo.newBuilder().setTaskId(taskId).setName("Hello World Task")
						.setSlaveId(Protos.SlaveID.newBuilder().setValue(offer.getSlaveId().getValue()))
						.addResources(Protos.Resource.newBuilder().setName("cpus").setType(Protos.Value.Type.SCALAR).setScalar(Protos.Value.Scalar.newBuilder().setValue(1)))
						.addResources(Protos.Resource.newBuilder().setName("mem").setType(Protos.Value.Type.SCALAR).setScalar(Protos.Value.Scalar.newBuilder().setValue(10)))
						.setExecutor(executorInfo).build();

				taskInfos.add(taskInfo);
				offersIds.add(offer.getId());
				schedulerDriver.launchTasks(offersIds, taskInfos);
			}
		}
		LOGGER.info("Finished Receiving Offers");
	}

	public void offerRescinded(SchedulerDriver schedulerDriver, Protos.OfferID offerID) {
		LOGGER.info("Offer Rescinded: " + offerID.getValue());
	}

	public void statusUpdate(SchedulerDriver schedulerDriver, Protos.TaskStatus taskStatus) {
		LOGGER.info("Status Update: " + taskStatus.getState().name());
	}

	public void frameworkMessage(SchedulerDriver schedulerDriver, Protos.ExecutorID executorID, Protos.SlaveID slaveID, byte[] bytes) {
		LOGGER.info("FrameworkMessage: " + bytes);
	}

	public void disconnected(SchedulerDriver schedulerDriver) {
		LOGGER.info("Disconnected");
	}

	public void slaveLost(SchedulerDriver schedulerDriver, Protos.SlaveID slaveID) {
		LOGGER.info("Slave Lost: " + slaveID);
	}

	public void executorLost(SchedulerDriver schedulerDriver, Protos.ExecutorID executorID, Protos.SlaveID slaveID, int i) {
		LOGGER.info("Executor Lost: " + executorID.getValue());
	}

	public void error(SchedulerDriver schedulerDriver, String s) {
		LOGGER.info("Scheduler Error: "+s);
	}

	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure(); // log4j setup
		LOGGER.info("Starting Scheduler");

		Scheduler scheduler = new EchoScheduler();
		Protos.FrameworkInfo frameworkInfo = Protos.FrameworkInfo.newBuilder().setUser("").setName("Echo Framework").build();

		MesosSchedulerDriver driver = new MesosSchedulerDriver(scheduler, frameworkInfo, args[0]);
		int status = driver.run() == Protos.Status.DRIVER_STOPPED ? 0 : 1;
		driver.stop(); //
		System.exit(status);
	}
}
