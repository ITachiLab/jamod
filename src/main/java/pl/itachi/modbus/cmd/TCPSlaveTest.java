//License
/***
 * Java Modbus Library (jamod)
 * Copyright (c) 2002-2004, jamod development team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the author nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ***/
package pl.itachi.modbus.cmd;

import pl.itachi.modbus.ModbusCoupler;
import pl.itachi.modbus.Modbus;
import pl.itachi.modbus.net.ModbusTCPListener;
import pl.itachi.modbus.procimg.SimpleDigitalIn;
import pl.itachi.modbus.procimg.SimpleDigitalOut;
import pl.itachi.modbus.procimg.SimpleInputRegister;
import pl.itachi.modbus.procimg.SimpleProcessImage;
import pl.itachi.modbus.procimg.SimpleRegister;


/**
 * Class implementing a simple Modbus/TCP slave.
 * A simple process image is available to test
 * functionality and behaviour of the implementation.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class TCPSlaveTest {


  public static void main(String[] args) {

    ModbusTCPListener listener = null;
    SimpleProcessImage spi = null;
    int port = Modbus.DEFAULT_PORT;
  
    try {
      if(args != null && args.length ==1) {
        port = Integer.parseInt(args[0]);
      }
      System.out.println("jModbus Modbus Slave (Server)");

      //1. prepare a process image
      spi = new SimpleProcessImage();

      spi.addDigitalOut(new SimpleDigitalOut(true));
      spi.addDigitalOut(new SimpleDigitalOut(true));

      spi.addDigitalIn(new SimpleDigitalIn(false));
      spi.addDigitalIn(new SimpleDigitalIn(true));
      spi.addDigitalIn(new SimpleDigitalIn(false));
      spi.addDigitalIn(new SimpleDigitalIn(true));
      // allow checking LSB/MSB order
      spi.addDigitalIn(new SimpleDigitalIn(true));
      spi.addDigitalIn(new SimpleDigitalIn(true));
      spi.addDigitalIn(new SimpleDigitalIn(true));
      spi.addDigitalIn(new SimpleDigitalIn(true));


      spi.addRegister(new SimpleRegister(251));
      spi.addInputRegister(new SimpleInputRegister(45));

      //2. create the coupler holding the image
      ModbusCoupler.getReference().setProcessImage(spi);
      ModbusCoupler.getReference().setMaster(false);
      ModbusCoupler.getReference().setUnitID(15);

      //3. create a listener with 3 threads in pool
      if (Modbus.debug) System.out.println("Listening...");
      listener = new ModbusTCPListener(3);
      listener.setPort(port);
      listener.start();

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }//main


}//class TCPSlaveTest
