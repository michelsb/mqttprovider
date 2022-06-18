package br.ufc.smartufc.mqttprovider;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import br.ufc.smartufc.mqttprovider.code.MqttPubSub;
import br.ufc.smartufc.mqttprovider.model.Actuator;
import br.ufc.smartufc.mqttprovider.model.TDSensor;
import br.ufc.smartufc.mqttprovider.util.Param;

public class App 
{
    public static void main( String[] args ) throws IOException
    {    	
    	
		String mosquitto_host = "localhost";//System.getenv("MOSQUITTO_HOST");
    	String mosquitto_port = "1883";//System.getenv("MOSQUITTO_PORT");
    	Param.address = "tcp://"+mosquitto_host+":"+mosquitto_port;
		Param.time_of_experiment = 60;
    	
    	CSVParser parser = new CSVParserBuilder().withSeparator(';').withIgnoreQuotations(true).build();    	
    	Reader readerTDSensors = Files.newBufferedReader(Paths.get("csv/tdsensors.csv"));
    	//Reader readerEDSensors = Files.newBufferedReader(Paths.get("csv/edsensors.csv"));
		Reader readerActuators = Files.newBufferedReader(Paths.get("csv/actuators.csv"));
        CSVReader csvReaderTDSensors = new CSVReaderBuilder(readerTDSensors).withSkipLines(1).withCSVParser(parser).build();
        //CSVReader csvReaderEDSensors = new CSVReaderBuilder(readerEDSensors).withSkipLines(1).withCSVParser(parser).build();
		CSVReader csvReaderActuators = new CSVReaderBuilder(readerActuators).withSkipLines(1).withCSVParser(parser).build();     	
    	
        List<String[]> tdSensors = csvReaderTDSensors.readAll();
        //List<String[]> edSensors = csvReaderEDSensors.readAll();
		List<String[]> actuators = csvReaderActuators.readAll();
        
		TDSensor[] tdSensorArray = null;
		if (tdSensors.size()>0) {
			tdSensorArray = new TDSensor[tdSensors.size()];        
			int index = 0; 
			TDSensor t = new TDSensor();
			for (String[] tdSensor : tdSensors) {
				t.setType(tdSensor[0]);
				t.setApiKey(tdSensor[1]);
				t.setDeviceId(tdSensor[2]);
				t.setPeriodicity(Integer.parseInt(tdSensor[3]));
				t.setNumberOfDevices(Integer.parseInt(tdSensor[4]));
				String[] array = new String[1];
				array[0] = tdSensor[5];
				t.setData(array);
				String[] max = new String [1];
				max[0] = tdSensor[6];
				t.setMax(max);
				String[] min = new String [1];
				min[0] = tdSensor[7];
				t.setMin(min);
				
				tdSensorArray[index] = t;
				index++;
				t = new TDSensor();
			}
		}       	
        
		Actuator[] actuatorArray = null;
		if (actuators.size()>0) {
			actuatorArray = new Actuator[actuators.size()];        
			int index = 0; 
			Actuator a = new Actuator();
			for (String[] actuator : actuators) {
				a.setType("s");
				HashMap<String,String> commands = new HashMap<String,String>();
				String[] cmds = actuator[0].split(",");				
				int seq = 0;
				int defaultState = Integer.parseInt(actuator[4]);
				for (String cmd : cmds) {
					String[] command = cmd.split(":");
					commands.put(command[0], command[1]);
					if (seq == defaultState) {
						a.setDefaultState(command[1]);
					}
					seq++;
				}
				a.setCommands(commands);
				a.setApiKey(actuator[1]);
				a.setDeviceId(actuator[2]);
				a.setPeriodicity(Integer.parseInt(actuator[3]));
				actuatorArray[index] = a;
				index++;
				a = new Actuator();
			}
		}  		

        System.out.println("RUN!! Sending to ip " + Param.address);
		System.out.println("Time of experiment (in seconds): " + Param.time_of_experiment);
		
		MqttPubSub mqttp = new MqttPubSub();
		mqttp.setTimeSensors(tdSensorArray);
		mqttp.setActuators(actuatorArray);
		Thread threadDoPdf = new Thread(mqttp);
        threadDoPdf.start();    
		
    }
}