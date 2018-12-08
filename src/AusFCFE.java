import javax.crypto.*;
import javax.crypto.spec.*;
import javax.swing.*;
import java.awt.event.ItemEvent;
import java.io.*;
import java.math.*;
import java.security.*;

public class AusFCFE implements WindowConstants
{
    private JPanel panelMain;
    private JButton encrypt;
    private JButton decrypt;
    private JTextField textField1;
    private JLabel key;
    private JButton fileOpen;
    private JButton setSaveButton;
    private JCheckBox saveKeyOnEncryptCheckBox;
    private File selectedLoadFile;
    private File selectedSaveFile;
    private boolean saveKey;

    private AusFCFE()
    {
        fileOpen.addActionListener(e -> open());
        encrypt.addActionListener(e -> encrypt());
        decrypt.addActionListener(e -> decrypt());
        setSaveButton.addActionListener(e -> save());
        textField1.setText(keyGen());
        saveKeyOnEncryptCheckBox.addItemListener(e -> saveKey = e.getStateChange() == ItemEvent.SELECTED);
    }

    /**
     * {@inheritDoc}
     *
     * @param args
     */
    public static void main(String args[])
    {
        JFrame frame = new JFrame();
        frame.setContentPane(new AusFCFE().panelMain);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void encrypt()
    {
        if (selectedLoadFile == null)
        {
            JOptionPane.showMessageDialog(null, "No File Loaded", "Error", JOptionPane.INFORMATION_MESSAGE);
        }
        if (selectedSaveFile == null)
        {
            JOptionPane.showMessageDialog(null, "No File Set To Save", "Error", JOptionPane.INFORMATION_MESSAGE);
        }
        fileProcessor(1, textField1.getText(), selectedLoadFile, selectedSaveFile, true);
    }

    private void decrypt()
    {
        if (selectedLoadFile == null)
        {
            JOptionPane.showMessageDialog(null, "No File Loaded", "Error", JOptionPane.INFORMATION_MESSAGE);
        }
        if (selectedSaveFile == null)
        {
            JOptionPane.showMessageDialog(null, "No File Set TO Save", "Error", JOptionPane.INFORMATION_MESSAGE);
        }
        fileProcessor(2, textField1.getText(), selectedLoadFile, selectedSaveFile, false);
    }

    private void open()
    {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        int result = jFileChooser.showOpenDialog(new JFrame());
        if (result == JFileChooser.APPROVE_OPTION)
        {
            selectedLoadFile = jFileChooser.getSelectedFile();
        }
    }

    private void save()
    {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        int result = jFileChooser.showSaveDialog(new JFrame());
        if (result == JFileChooser.APPROVE_OPTION)
        {
            selectedSaveFile = jFileChooser.getSelectedFile();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param cipherMode
     * @param key
     * @param inputFile
     * @param outputFile
     * @param encrypt
     */
    private void fileProcessor(int cipherMode, String key, File inputFile, File outputFile, boolean encrypt)
    {
        try
        {
            Key secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(cipherMode, secretKey);
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
            byte[] outputBytes = cipher.doFinal(inputBytes);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
            System.out.println(inputStream.read(inputBytes));
            inputStream.close();
            outputStream.close();
        }
        catch (NullPointerException | IllegalArgumentException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | IOException e)
        {
            JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.INFORMATION_MESSAGE);
        }
        if (encrypt && saveKey)
        {
            writeKey(key);
        }
    }

    private static String keyGen()
    {
        String key = null;
        try
        {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128); /* 128-bit AES */
            SecretKey s = kg.generateKey();
            byte[] binary = s.getEncoded();
            key = String.format("%032X", new BigInteger(+1, binary));
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return key;
    }

    /**
     * {@inheritDoc}
     *
     * @param key
     */
    private void writeKey(String key)
    {
        File file = new File(selectedSaveFile.getAbsolutePath() + "_key.txt");
        try
        {
            FileWriter writer = new FileWriter(file);
            writer.write(key);
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
