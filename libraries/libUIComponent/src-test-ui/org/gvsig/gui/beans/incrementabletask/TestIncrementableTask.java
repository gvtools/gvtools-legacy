package org.gvsig.gui.beans.incrementabletask;

import org.gvsig.gui.beans.progresspanel.LogControl;

/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Government (CIT)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 */

/**
 * Test del IncrementableTask
 *
 * @version 27/05/2007
 * @author BorSanZa - Borja Sánchez Zamorano (borja.sanchez@iver.es)
 */
public class TestIncrementableTask {
	class ClassProcess implements Runnable, IIncrementable, IncrementableListener {
		int i = 0;
		long j = 0;
		LogControl log = new LogControl();
		IncrementableTask incrementableTask = null;

		private volatile Thread blinker;
//		private boolean ended = false;
//		private boolean threadSuspended = false;

		public ClassProcess() {
		}

		public void start() {
			blinker = new Thread(this);
			blinker.start();
		}

		public synchronized void stop() {
//			ended = true;
			blinker = null;
			notify();
		}

		public boolean isAlive() {
			return blinker.isAlive();
		}

		public void procesoDuro() throws InterruptedException {
			try {
				for (long k = 0; k <= 8000; k++)
					for (long n = 0; n <= 5000; n++) {
						for (long l = 0; l <= 100; l++);
						if (Thread.currentThread().isInterrupted())
							throw new InterruptedException();
					}
				System.out.println("b");
			} finally {
				System.out.println("c");
			}
		}

		public synchronized void run() {
			try {
				procesoDuro();
			} catch (InterruptedException e1) {
				System.out.println("Se ha salido");
			}
/*
			for (long k=0; k<=65535; k++)
				for (long n=0; n<=5000; n++);
			for (j=0; j<=65535; j++) {
				if (Thread.currentThread().isInterrupted())
					break;
				for (long i=0; i<=65535; i++);
				log.replaceLastLine(j + "");
				System.out.println(Thread.currentThread().isInterrupted());
				if ((j%1000)==0) {
					log.addLine(j + "");
				}
				synchronized(this) {
					while (threadSuspended && !Thread.currentThread().isInterrupted())
						try {
							wait(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
				}
			}*/
			incrementableTask.processFinalize();
		}

		public String getLabel() {
			return "Generando estadísticas, por favor, espere...";
		}

		public String getLog() {
			return log.getText();
		}

		public int getPercent() {
			return (int) ((j*100)/65535);
		}

		public String getTitle() {
			return "Barra de progreso";
		}

		public void setIncrementableTask(IncrementableTask value) {
			incrementableTask = value;
		}

		public void actionCanceled(IncrementableEvent e) {
//			ended = true;
			blinker.interrupt();
		}

		public void actionResumed(IncrementableEvent e) {
//			threadSuspended = false;
		}

		public void actionSuspended(IncrementableEvent e) {
//			threadSuspended = true;
		}


		public void setLabel(String label) {
			// TODO Auto-generated method stub
			
		}

		public boolean isSuspended() {
			// TODO Auto-generated method stub
			return false;
		}

		public void resume() {
			// TODO Auto-generated method stub
			
		}

		public void suspend() {
			// TODO Auto-generated method stub
			
		}

		public boolean isCancelable() {
			// TODO Auto-generated method stub
			return false;
		}

		public void process() throws InterruptedException, Exception {
			// TODO Auto-generated method stub
			
		}

		public void setCancelable(boolean b) {
			// TODO Auto-generated method stub
			
		}

		public boolean isPausable() {
			// TODO Auto-generated method stub
			return false;
		}

		public void setPausable(boolean b) {
			// TODO Auto-generated method stub
			
		}
	}

	ClassProcess classProcess = null;

	public TestIncrementableTask() {
		super();
		initialize();
	}

	private void initialize() {
		classProcess = new ClassProcess();
		IncrementableTask incrementableTask = new IncrementableTask(classProcess);
		classProcess.setIncrementableTask(incrementableTask);
		incrementableTask.showWindow();
		incrementableTask.addIncrementableListener(classProcess);

		incrementableTask.start();
		classProcess.start();
		while (classProcess.isAlive()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		classProcess.stop();
		classProcess = null;
		incrementableTask = null;
	}

	public static void main(String[] args) {
		new TestIncrementableTask();
	}
}