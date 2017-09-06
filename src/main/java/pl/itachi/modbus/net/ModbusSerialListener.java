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
package pl.itachi.modbus.net;

import pl.itachi.modbus.Modbus;
import pl.itachi.modbus.ModbusDevice;
import pl.itachi.modbus.ModbusIOException;
import pl.itachi.modbus.io.ModbusTransport;
import pl.itachi.modbus.msg.ModbusRequest;
import pl.itachi.modbus.msg.ModbusResponse;
import pl.itachi.modbus.util.SerialParameters;

import java.util.List;
import java.util.Optional;

/**
 * Class that implements a ModbusTCPListener.<br>
 * If listening, it accepts incoming requests
 * passing them on to be handled.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class ModbusSerialListener {

    //Members
    private boolean m_Listening;                //Flag for toggling listening/!listening
    private SerialConnection m_SerialCon;
    private static int c_RequestCounter = 0;          //counter for amount of requests
    private List<ModbusDevice> devices;

    /**
     * Constructs a new <tt>ModbusSerialListener</tt> instance.
     *
     * @param params a <tt>SerialParameters</tt> instance.
     * @param devices
     */
    public ModbusSerialListener(SerialParameters params, List<ModbusDevice> devices) {
        this.m_SerialCon = new SerialConnection(params);
        this.devices = devices;

        listen();
    }//constructor

    /**
     * Listen to incoming messages.
     */
    private void listen() {
        try {
            m_Listening = true;
            m_SerialCon.open();
            ModbusTransport transport = m_SerialCon.getModbusTransport();
            do {
                if (m_Listening) {
                    try {
                        //1. read the request
                        ModbusRequest request = transport.readRequest();
                        ModbusResponse response = null;

                        Optional<ModbusDevice> device = devices.stream().filter(d -> d.canHandleRequest(request)).findFirst();

                        if (device.isPresent() && device.get().getProcessImage() != null) {
                            response = request.createResponse(device.get());
                        } else {
                            response = request.createExceptionResponse(Modbus.ILLEGAL_FUNCTION_EXCEPTION);
                        }

                        if (Modbus.debug) {
                            System.out.println("Request:" + request.getHexMessage());
                        }
                        if (Modbus.debug) {
                            System.out.println("Response:" + response.getHexMessage());
                        }

                        transport.writeMessage(response);

                        count();
                    } catch (ModbusIOException ex) {
                        ex.printStackTrace();
                        continue;
                    }
                }
                //ensure nice multithreading behaviour on specific platforms

            } while (true);

        } catch (Exception e) {
            //FIXME: this is a major failure, how do we handle this
            e.printStackTrace();
        }
    }//listen

    /**
     * Sets the listening flag of this <tt>ModbusTCPListener</tt>.
     *
     * @param b true if listening (and accepting incoming connections),
     *          false otherwise.
     */
    public void setListening(boolean b) {
        m_Listening = b;
    }//setListening

    /**
     * Tests if this <tt>ModbusTCPListener</tt> is listening
     * and accepting incoming connections.
     *
     * @return true if listening (and accepting incoming connections),
     * false otherwise.
     */
    public boolean isListening() {
        return m_Listening;
    }//isListening

    private void count() {
        c_RequestCounter++;
        if (c_RequestCounter == REQUESTS_TOGC) {
            System.gc();
            c_RequestCounter = 0;
        }
    }//count

    private static final int REQUESTS_TOGC = 15;

}//class ModbusTCPListener
