package cl.corona.integrationgroupg.service;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

@Service
public class intUpload {
    @Value("${sftpd.ip}")
    private String d_sftpip;

    @Value("${sftpd.prt}")
    private int d_sftpprt;

    @Value("${sftpd.usr}")
    private String d_sftpusr;

    @Value("${sftpd.pss}")
    private String d_sftppss;

    @Value("${sftpd.org}")
    private String d_sftporg;

    @Value("${sftpd.dst_ATR}")
    private String d_sftpdtn_ATR;

    @Value("${sftpd.dst_PRD}")
    private String d_sftpdtn_PRD;


    @Value("${sftpd.dst_RCV}")
    private String d_sftpdtn_RCV;

    @Value("${sftpd.dst_MTCPRD}")
    private String d_sftpdtn_MTCPRD;

    @Value("${sftpd.dst_OC}")
    private String d_sftpdtn_OC;

    @Value("${name.file}")
    private String d_namefile;

    @Value("${separador.carpetas}")
    private String separador;

    @Value("${largo.archivo}")
    private int largo_archivo;

    private static final Logger LOG = LoggerFactory.getLogger(intUpload.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    String strDir = System.getProperty("user.dir");

    public void UploadFile() throws IOException {

        JSch jsch = new JSch();
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("PreferredAuthentications", "password");
        jsch.setConfig(config);

        try {

            Session session = jsch.getSession(d_sftpusr, d_sftpip, d_sftpprt);
            session.setConfig("PreferredAuthentications", "password");
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(d_sftppss);
            session.connect();
            Channel channel = session.openChannel("sftp");
            ChannelSftp d_sftp = (ChannelSftp) channel;
            d_sftp.connect();

            final String path = strDir + separador + d_sftporg;
            //final String path = sftporg;

            File directory = new File(path);
            File[] fList = directory.listFiles();

            for (File file : fList) {

                String name = StringUtils.getFilename(file.getName());
                int end = name.indexOf("_");
                String sSubCadena = name.substring(0, end).toUpperCase();

                //if (sSubCadena.equals(d_namefile)) {

                switch (sSubCadena) {
                    //FLUJO ATR
                    case "SDIBASATR":
                        if (file.isFile()) {
                            String filename = file.getAbsolutePath();
                            //System.out.println(filename + " transfered to --> " + sftpdtn);
                            LOG.info("Uploading ATR " + filename + " ---> " + d_sftpdtn_ATR);
                            d_sftp.put(filename, d_sftpdtn_ATR);
                            file.delete();
                            LOG.info("{} : Upload Ok", dateTimeFormatter.format(LocalDateTime.now()));
                        }
                        break;
                    //FLUJO PRODUCTO
                    case "SDIPRDMST":
                        if (file.isFile()) {
                            String filename = file.getAbsolutePath();
                            //System.out.println(filename + " transfered to --> " + sftpdtn);
                            LOG.info("Uploading PRD " + filename + " ---> " + d_sftpdtn_PRD);
                            d_sftp.put(filename, d_sftpdtn_PRD);
                            file.delete();
                            LOG.info("{} : Upload Ok", dateTimeFormatter.format(LocalDateTime.now()));
                        }
                        break;

                    case "MTCSDIPRDMST":
                        if (file.isFile()) {
                            String filename = file.getAbsolutePath();
                            //System.out.println(filename + " transfered to --> " + sftpdtn);
                            LOG.info("Uploading MTCPRD " + filename + " ---> " + d_sftpdtn_MTCPRD);
                            d_sftp.put(filename, d_sftpdtn_MTCPRD);
                            file.delete();
                            LOG.info("{} : Upload Ok", dateTimeFormatter.format(LocalDateTime.now()));
                        }
                        break;

                    case "SDIPMGHDE":
                        if (file.isFile()) {
                            String filename = file.getAbsolutePath();
                            //System.out.println(filename + " transfered to --> " + sftpdtn);
                            LOG.info("Uploading OC " + filename + " ---> " + d_sftpdtn_OC);
                            d_sftp.put(filename, d_sftpdtn_OC);
                            file.delete();
                            LOG.info("{} : Upload Ok", dateTimeFormatter.format(LocalDateTime.now()));
                        }
                        break;

                    case "SDIRCVSHE":
                        if (file.isFile()) {
                            String filename = file.getAbsolutePath();
                            //System.out.println(filename + " transfered to --> " + sftpdtn);
                            LOG.info("Uploading RCV " + filename + " ---> " + d_sftpdtn_RCV);
                            d_sftp.put(filename, d_sftpdtn_RCV);
                            file.delete();
                            LOG.info("{} : Upload Ok", dateTimeFormatter.format(LocalDateTime.now()));
                        }
                        break;

                }
            }

            d_sftp.exit();
            channel.disconnect();
            session.disconnect();

        } catch (JSchException e) {
            LOG.error("No se pudo realizar la conexión ,{}", e);
        } catch (SftpException e) {
            e.printStackTrace();
        }

    }
}
