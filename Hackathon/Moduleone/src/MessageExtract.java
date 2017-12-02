 import java.awt.image.*;
 import javax.swing.*;
 import java.awt.*;
 import java.awt.event.*;
 import javax.imageio.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

 public class MessageExtract extends JFrame implements ActionListener
 {
 JButton open = new JButton("Open"), decode = new JButton("Decode"),
    reset = new JButton("Reset"),embed = new JButton("Embed");
 JTextArea message = new JTextArea(10,3);
 BufferedImage image = null;
 JScrollPane imagePane = new JScrollPane();

 public MessageExtract() {
    super("Decode stegonographic message in image");
    assembleInterface();
   
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);  
    this.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().
       getMaximumWindowBounds());
    this.setVisible(true);
    }

 private void assembleInterface() {
    JPanel p = new JPanel(new FlowLayout());
    p.add(open);
    p.add(decode);
    p.add(embed);
   p.add(reset);
    this.getContentPane().add(p, BorderLayout.NORTH);
    open.addActionListener(this);
    decode.addActionListener(this);
    reset.addActionListener(this);
    embed.addActionListener(this);
    open.setMnemonic('O');
    decode.setMnemonic('D');
    embed.setMnemonic('E');
    reset.setMnemonic('R');
   
    p = new JPanel(new GridLayout(1,1));
    p.add(new JScrollPane(message));
    message.setFont(new Font("Arial",Font.BOLD,20));
    p.setBorder(BorderFactory.createTitledBorder("Decoded message"));
    message.setEditable(false);
    this.getContentPane().add(p, BorderLayout.SOUTH);
   
    imagePane.setBorder(BorderFactory.createTitledBorder("Steganographed Image"));
    this.getContentPane().add(imagePane, BorderLayout.CENTER);
    }

 public void actionPerformed(ActionEvent ae) {
    Object o = ae.getSource();
    if(o == open)
       openImage();
    else if(o == decode)
       MessageExtract();
    else if(o == reset)
       resetInterface();
    else if(o == embed){
    	dispose();
    	new MessageAdd();
    }
    
     
    }

 private java.io.File showFileDialog(boolean open) {
    JFileChooser fc = new JFileChooser("Open an image");
    javax.swing.filechooser.FileFilter ff = new javax.swing.filechooser.FileFilter() {
       public boolean accept(java.io.File f) {
          String name = f.getName().toLowerCase();
          return f.isDirectory() ||   name.endsWith(".png") || name.endsWith(".bmp");
          }
       public String getDescription() {
          return "Image (*.png, *.bmp)";
          }
       };
    fc.setAcceptAllFileFilterUsed(false);
    fc.addChoosableFileFilter(ff);

    java.io.File f = null;
    if(open && fc.showOpenDialog(this) == fc.APPROVE_OPTION)
       f = fc.getSelectedFile();
    else if(!open && fc.showSaveDialog(this) == fc.APPROVE_OPTION)
       f = fc.getSelectedFile();
    return f;
    }

 private void openImage() {
    java.io.File f = showFileDialog(true);
    try {  
       image = ImageIO.read(f);
       JLabel l = new JLabel(new ImageIcon(image));
       imagePane.getViewport().add(l);
       this.validate();
       } catch(Exception ex) { ex.printStackTrace(); }
    }

 private void MessageExtract() {
 	
    int len = extractInteger(image, 0, 0);
    
    byte b[] = new byte[len];
    
    for(int i=0; i<len; i++){
    	b[i] = extractByte(image, i*16+32, 0);
    	System.out.println(b[i]);
    }
       
    message.setText(new String(b));
    }

 private int extractInteger(BufferedImage img, int start, int storageBit) {
 	
    int maxX = img.getWidth(), maxY = img.getHeight(),
       startX = start/maxY, startY = start - startX*maxY, count=0;
       
   int length = 0;
   
    for(int i=startX; i<maxX && count<32; i++) {
       for(int j=startY; j<maxY && count<32; j++) {
          int rgb = img.getRGB(i, j), bit = getBitValue(rgb, storageBit);
          length = setBitValue(length, count, bit);
          count++;
          }
       }
    return length;
    }
int even = 0;
 private byte extractByte(BufferedImage img, int start, int storageBit) {
 	
    int maxX = img.getWidth(), maxY = img.getHeight(),
       startX = start/maxY, startY = start - startX*maxY, count=0;
       
    byte b = 0;
    
    for(int i=startX; i<maxX && count<8; i++) {
       for(int j=startY; j<maxY && count<8; j++) {
          	
          	if(even%2 == 0)
          	{
          		int rgb = img.getRGB(i, j), bit = getBitValue(rgb, storageBit);
          		b = (byte)setBitValue(b, count, bit);
         		 System.out.println("Binary "+b);
         		 count++; 
         		 even = 1;
          	} else {
          		even = 0;
          	}
          	even = 0;
          	
          }
       }
      
    int bb = b;
    
    if(bb%2 == 0)
         bb-=2;
     else
         bb+=2;
      
    return (byte)bb;
    }
 private void resetInterface() {
    message.setText("");
    imagePane.getViewport().removeAll();
    image = null;
    this.validate();
 }

 private int getBitValue(int n, int location) {   // Get Bit
    int v = n & (int) Math.round(Math.pow(2, location));
    
    System.out.println("n "+n+" Location "+location+" V "+v);
    return v==0?0:1;
    }

 private int setBitValue(int n, int location, int bit) {  // Set Bit
    int toggle = (int) Math.pow(2, location), bv = getBitValue(n, location);
    if(bv == bit)
       return n;
    if(bv == 0 && bit == 1)
      n |= toggle;
    else if(bv == 1 && bit == 0)
       n ^= toggle;
    return n;
    
    }
 }