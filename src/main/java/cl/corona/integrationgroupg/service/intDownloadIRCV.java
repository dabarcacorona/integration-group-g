package cl.corona.integrationgroupg.service;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Vector;

@Service
public class intDownloadIRCV {
    @Value("${sftpocmx2.ip}")
    private String sftpip;

    @Value("${sftpocmx2.prt}")
    private int sftpprt;

    @Value("${sftpocmx2.usr}")
    private String sftpusr;

    @Value("${sftpocmx2.pss}")
    private String sftppss;

    @Value("${sftpocmx2.org_IRCV}")
    private String sftporg_IRCV;

    @Value("${sftpocmx2.org_INVOICE}")
    private String sftporg_INVOICE;

    @Value("${sftpocmx2.dst}")
    private String sftpdtn;

    @Value("${name.file}")
    private String namefile;

    @Value("${separador.carpetas}")
    private String separador;

    @Value("${largo.archivo}")
    private int largo_archivo;

    private static final Logger LOG = LoggerFactory.getLogger(intDownload.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    String strDir = System.getProperty("user.dir");

    public void DownloadFile() throws IOException {

        JSch jsch = new JSch();
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        config.put("PreferredAuthentications", "password");
        jsch.setConfig(config);

        try {

            Session session = jsch.getSession(sftpusr, sftpip, sftpprt);
            session.setConfig("PreferredAuthentications", "password");
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(sftppss);
            session.connect();
            Channel channel = session.openChannel("sftp");
            ChannelSftp sftp = (ChannelSftp) channel;
            sftp.connect();

            final String path = strDir + separador + sftpdtn + separador;
            //final String path = sftpdtn;
            //LOG.info(path);

            Vector<ChannelSftp.LsEntry> entries = sftp.ls(sftporg_IRCV);

            //download all files (except the ., .. and folders) from given folder
            for (ChannelSftp.LsEntry en : entries) {
                if (en.getFilename().equals(".") || en.getFilename().equals("..") || en.getAttrs().isDir()) {
                    continue;
                }

                String filename = StringUtils.getFilename(en.getFilename());
                //String sSubCadena = filename.substring(0, largo_archivo).toUpperCase();
                int end = filename.indexOf("_");
                String sSubCadena = filename.substring(0, end).toUpperCase();

                if (sSubCadena.equals("CMX")) {
                    LOG.info("Downloading IRCV " + (sftporg_IRCV + en.getFilename()) + " ---> " + path + en.getFilename());
                    sftp.get(sftporg_IRCV + en.getFilename(), path + en.getFilename());
                    sftp.rm(sftporg_IRCV + en.getFilename());
                    LOG.info("{} : Download Ok", dateTimeFormatter.format(LocalDateTime.now()));

                }
            }

            //-------------------------------------------------------------------------------------------//

            Vector<ChannelSftp.LsEntry> entries2 = sftp.ls(sftporg_INVOICE);

            //download all files (except the ., .. and folders) from given folder
            for (ChannelSftp.LsEntry en : entries2) {
                if (en.getFilename().equals(".") || en.getFilename().equals("..") || en.getAttrs().isDir()) {
                    continue;
                }

                String filename = StringUtils.getFilename(en.getFilename());
                //String sSubCadena = filename.substring(0, largo_archivo).toUpperCase();
                int end = filename.indexOf("_");
                String sSubCadena = filename.substring(0, end).toUpperCase();

                if (sSubCadena.equals("CMX")) {
                    LOG.info("Downloading INVOICE " + (sftporg_INVOICE + en.getFilename()) + " ---> " + path + en.getFilename());
                    sftp.get(sftporg_INVOICE + en.getFilename(), path + en.getFilename());
                    sftp.rm(sftporg_INVOICE + en.getFilename());
                    LOG.info("{} : Download Ok", dateTimeFormatter.format(LocalDateTime.now()));

                }
            }

            sftp.exit();
            channel.disconnect();
            session.disconnect();

        } catch (JSchException e) {
            LOG.error("No se pudo realizar la conexión ,{}",  e);
        } catch (SftpException e) {
            e.printStackTrace();
        }

    }
}
