package cl.corona.integrationgroupg.setService;

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
public class setUpload {

    @Value("${sftpds.ip}")
    private String d_sftpip;

    @Value("${sftpds.prt}")
    private int d_sftpprt;

    @Value("${sftpds.usr}")
    private String d_sftpusr;

    @Value("${sftpds.pss}")
    private String d_sftppss;

    @Value("${sftpds.org}")
    private String d_sftporg;

    @Value("${sftpds.dst}")
    private String d_sftpdst;

    @Value("${name.file}")
    private String d_namefile;

    @Value("${separador.carpetas}")
    private String separador;

    @Value("${largo.archivo}")
    private int largo_archivo;

    private static final Logger LOG = LoggerFactory.getLogger(setUpload.class);
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

                    case "SDITRFDTI":
                        if (file.isFile()) {
                            String filename = file.getAbsolutePath();
                            LOG.info("Uploading Files ITRF " + filename + " ---> " + d_sftpdst);
                            d_sftp.put(filename, d_sftpdst);
                            file.delete();
                            LOG.info("{} : Upload Ok", dateTimeFormatter.format(LocalDateTime.now()));
                        }
                        break;
                    case "SDIRCVHDI":
                        if (file.isFile()) {
                            String filename = file.getAbsolutePath();
                            LOG.info("Uploading Files IRCV " + filename + " ---> " + d_sftpdst);
                            d_sftp.put(filename, d_sftpdst);
                            file.delete();
                            LOG.info("{} : Upload Ok", dateTimeFormatter.format(LocalDateTime.now()));
                        }
                        break;

                    //FLUJO OC COMEX
                    case "SDIPMGHDI":
                        if (file.isFile()) {
                            String filename = file.getAbsolutePath();
                            LOG.info("Uploading Files OC CMX " + filename + " ---> " + d_sftpdst);
                            d_sftp.put(filename, d_sftpdst);
                            file.delete();
                            LOG.info("{} : Upload Ok", dateTimeFormatter.format(LocalDateTime.now()));
                        }
                        break;

                    //FLUJO CMX VPC COMEX
                    case "CMX":
                        if (file.isFile()) {
                            String filename = file.getAbsolutePath();
                            LOG.info("Uploading Files VPC CMX " + filename + " ---> " + d_sftpdst);
                            d_sftp.put(filename, d_sftpdst);
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
