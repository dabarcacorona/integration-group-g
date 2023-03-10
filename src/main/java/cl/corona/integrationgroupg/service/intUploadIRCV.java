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
public class intUploadIRCV {
    @Value("${sftpdcmx2.ip}")
    private String d_sftpip;

    @Value("${sftpdcmx2.prt}")
    private int d_sftpprt;

    @Value("${sftpdcmx2.usr}")
    private String d_sftpusr;

    @Value("${sftpdcmx2.pss}")
    private String d_sftppss;

    @Value("${sftpdcmx2.org}")
    private String d_sftporg;

    @Value("${sftpdcmx2.dst_IRCV}")
    private String d_sftpdtn_IRCV;

    @Value("${sftpdcmx2.dst_INVOICE}")
    private String d_sftpdtn_INVOICE;


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
                String sSubCadena = name.substring(0, 10).toUpperCase();

                //if (sSubCadena.equals(d_namefile)) {

                switch (sSubCadena) {
                    //FLUJO IRCV
                    case "CMX_SDIRCV":
                        if (file.isFile()) {
                            String filename = file.getAbsolutePath();
                            //System.out.println(filename + " transfered to --> " + sftpdtn);
                            LOG.info("Uploading IRCV " + filename + " ---> " + d_sftpdtn_IRCV);
                            d_sftp.put(filename, d_sftpdtn_IRCV);
                            file.delete();
                            LOG.info("{} : Upload Ok", dateTimeFormatter.format(LocalDateTime.now()));
                        }
                        break;

                    case "CMX_INVOIC":
                        if (file.isFile()) {
                            String filename = file.getAbsolutePath();
                            //System.out.println(filename + " transfered to --> " + sftpdtn);
                            LOG.info("Uploading INVOICE " + filename + " ---> " + d_sftpdtn_INVOICE);
                            d_sftp.put(filename, d_sftpdtn_INVOICE);
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
