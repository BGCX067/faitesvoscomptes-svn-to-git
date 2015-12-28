package cal.accountapp.gestion;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.app.Activity;

public class Properties extends Activity{

	public final static int NOTIFICATION_ENABLED=0x01;
	public final static int NOTIFICATION_DISABLED=0x02;
	
	public final static int SETTINGS_LANG_NO=0x10;
	public final static int SETTINGS_LANG_FR=0x20;
	public final static int SETTINGS_LANG_EN=0x40;
	
	public final static int PASSWORD_ENABLED=0x100;
	public final static int PASSWORD_DISABLED=0x200;
	
	public static boolean enableNotifChecked=true;
	
	public static boolean enableForceLangChecked=false;
	public static boolean forceLangENChecked=false;
	public static boolean forceLangFRChecked=false;
	
	public static String currency="e";
	
	
	public static boolean passwordChecked=false;
	
	public static String oldPassword=""; 
	
	
	public static boolean enable_notification=true;
	public static int forceLang=0;
	
	public static double seuilNotifValue=0.0;
	public static boolean enablenotifSeuil=false;
	
	
	public static final String MODE_LEC="-";
	public static final String MODE_ECR="+";
	
	public Properties(){}
	
	public void GoProperties(String mode, File fl) {
		
		String data="";
		
		
		
		if(mode.equals(MODE_ECR))
		{
			//File ls = this.getFilesDir();
			

				try {
					FileOutputStream fos = new FileOutputStream(fl);
					DataOutputStream dos = new DataOutputStream(fos);
					
					data="enableNotifChecked="+enableNotifChecked+"\n";
					dos.write(data.getBytes());
					data="enableForceLangChecked="+enableForceLangChecked+"\n";
					dos.write(data.getBytes());
					data="forceLangENChecked="+forceLangENChecked+"\n";
					dos.write(data.getBytes());
					data="forceLangFRChecked="+forceLangFRChecked+"\n";
					dos.write(data.getBytes());
					data="passwordChecked="+passwordChecked+"\n";
					dos.write(data.getBytes());
					data="enable_notification="+enable_notification+"\n";
					dos.write(data.getBytes());
					data="forceLang="+forceLang+"\n";
					dos.write(data.getBytes());
					data="currency="+currency+"\n";
					dos.write(data.getBytes());
					data="seuilnotifvalue="+seuilNotifValue+"\n";
					dos.write(data.getBytes());
					data="enablenotifseuil="+enablenotifSeuil+"\n";
					dos.write(data.getBytes());
					
					fos.close();
				} catch (Exception ex) {	}
				
		}
		else
		{
			if(fl.length()!=0)
			{
				//File ls = this.getFilesDir();
				try {
					FileInputStream fis  = new FileInputStream(fl);
					DataInputStream dis = new DataInputStream(fis);
					
					data=dis.readLine();
					enable_notification=Boolean.parseBoolean(data.substring(data.indexOf("=")+1));
					data=dis.readLine();
					enableForceLangChecked=Boolean.parseBoolean(data.substring(data.indexOf("=")+1));
					data=dis.readLine();
					forceLangENChecked=Boolean.parseBoolean(data.substring(data.indexOf("=")+1));
					data=dis.readLine();
					forceLangFRChecked=Boolean.parseBoolean(data.substring(data.indexOf("=")+1));
					data=dis.readLine();
					passwordChecked=Boolean.parseBoolean(data.substring(data.indexOf("=")+1));
					data=dis.readLine();
					enable_notification=Boolean.parseBoolean(data.substring(data.indexOf("=")+1));
					data=dis.readLine();
					forceLang=Integer.parseInt(data.substring(data.indexOf("=")+1));
					data=dis.readLine();
					currency=data.substring(data.indexOf("=")+1);
					data=dis.readLine();
					seuilNotifValue=Double.parseDouble(data.substring(data.indexOf("=")+1));
					data=dis.readLine();
					enablenotifSeuil=Boolean.parseBoolean(data.substring(data.indexOf("=")+1));
					
					fis.close();
				} catch (Exception e) {			}
			}
			else
			{
				try {
				FileOutputStream fos = new FileOutputStream(fl);
				DataOutputStream dos = new DataOutputStream(fos);
				
				data="enableNotifChecked="+true+"\n";
				dos.write(data.getBytes());
				data="enableForceLangChecked="+false+"\n";
				dos.write(data.getBytes());
				data="forceLangENChecked="+false+"\n";
				dos.write(data.getBytes());
				data="forceLangFRChecked="+false+"\n";
				dos.write(data.getBytes());
				data="passwordChecked="+false+"\n";
				dos.write(data.getBytes());
				data="enable_notification="+true+"\n";
				dos.write(data.getBytes());
				data="forceLang="+0+"\n";
				dos.write(data.getBytes());
				data="currency="+"e"+"\n";
				dos.write(data.getBytes());
				data="seuilnotifvalue="+0.0+"\n";
				dos.write(data.getBytes());
				data="enablenotifseuil="+false+"\n";
				dos.write(data.getBytes());
				
				fos.close();
				} catch (Exception exx) {	}
			}
		}
	}
	
	
}
