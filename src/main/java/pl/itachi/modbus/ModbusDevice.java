package pl.itachi.modbus;

import pl.itachi.modbus.msg.ModbusRequest;
import pl.itachi.modbus.procimg.SimpleProcessImage;

public class ModbusDevice {
    private SimpleProcessImage processImage;
    private int unitID;
    private boolean isMaster;

    public boolean canHandleRequest(ModbusRequest request) {
        return request.getUnitID() == this.unitID;
    }

    public SimpleProcessImage getProcessImage() {
        return processImage;
    }

    public void setProcessImage(SimpleProcessImage processImage) {
        this.processImage = processImage;
    }

    public int getUnitID() {
        return unitID;
    }

    public void setUnitID(int unitID) {
        this.unitID = unitID;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public void setMaster(boolean master) {
        isMaster = master;
    }
}
