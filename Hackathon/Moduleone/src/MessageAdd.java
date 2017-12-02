 import java.awt.image.*;
 import javax.swing.*;
 import java.awt.*;
 import java.awt.event.*;
 import javax.imageio.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

 public class MessageAdd extends JFrame implements ActionListener
 {
 JButton open = new JButton("Open"), embed = new JButton("Embed"),
    save = new JButton("Save into new file"),decode = new JButton("Decode"), reset = new JButton("Reset");
    
 JTextArea message = new JTextArea(10,3);
 
 BufferedImage sourceImage = null, embeddedImage = null;
 
 JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
 JScrollPane originalPane = new JScrollPane(),
    embeddedPane = new JScrollPane();
    

 public MessageAdd() {
    super("Embed stegonographic message in image");
    assembleInterface();

    this.setDefaultCloseOperation(EXIT_ON_CLOSE);  
    this.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().
      getMaximumWindowBounds());
    this.setVisible(true);
    sp.setDividerLocation(0.5);
    this.validate();
    }

 private void assembleInterface() {
    JPanel p = new JPanel(new FlowLayout());
    p.add(open);
    p.add(embed);
    p.add(decode);
    p.add(save);  
    p.add(reset);
    this.getContentPane().add(p, BorderLayout.SOUTH);
    open.addActionListener(this);
    embed.addActionListener(this);
    save.addActionListener(this);  
    reset.addActionListener(this);
    decode.addActionListener(this);
    open.setMnemonic('O');
    embed.setMnemonic('E');
    save.setMnemonic('S');
    reset.setMnemonic('R');
    decode.setMnemonic('D');
   
    p = new JPanel(new GridLayout(1,1));
    p.add(new JScrollPane(message));
    message.setFont(new Font("Arial",Font.BOLD,20));
    p.setBorder(BorderFactory.createTitledBorder("Message to be embedded"));
    this.getContentPane().add(p, BorderLayout.NORTH);
  
    sp.setLeftComponent(originalPane);
    sp.setRightComponent(embeddedPane);
    originalPane.setBorder(BorderFactory.createTitledBorder("Original Image"));
    embeddedPane.setBorder(BorderFactory.createTitledBorder("Steganographed Image"));
    this.getContentPane().add(sp, BorderLayout.CENTER);
    }

 public void actionPerformed(ActionEvent ae) {
    Object o = ae.getSource();
    if(o == open)
       openImage();
    else if(o == embed)
       MessageAdd();
    else if(o == save)
       saveImage();
    else if(o == reset)
       resetInterface();
     else if(o == decode){
     dispose();
     	new MessageExtract();
     }
     	
    }
    

 private java.io.File showFileDialog(final boolean open) {
    JFileChooser fc = new JFileChooser("Open an image");
    javax.swing.filechooser.FileFilter ff = new javax.swing.filechooser.FileFilter() {
    	
       public boolean accept(java.io.File f) {
          String name = f.getName().toLowerCase();
          
          if(open) // if true
             return f.isDirectory() || name.endsWith(".jpg") || name.endsWith(".jpeg") ||
                name.endsWith(".png") || name.endsWith(".gif") || name.endsWith(".tiff") ||
                name.endsWith(".bmp") || name.endsWith(".dib");
                
          return f.isDirectory() || name.endsWith(".png") ||    name.endsWith(".bmp");
          }
          
       public String getDescription() {
          
          if(open)  // true
             return "Image (*.jpg, *.jpeg, *.png, *.gif, *.tiff, *.bmp, *.dib)";
             
          return "Image (*.png, *.bmp)";
          
          }
       };
    fc.setAcceptAllFileFilterUsed(false);
    fc.addChoosableFileFilter(ff);

    java.io.File f = null;   // Get File as Image
    if(open && fc.showOpenDialog(this) == fc.APPROVE_OPTION)
       f = fc.getSelectedFile();
    else if(!open && fc.showSaveDialog(this) == fc.APPROVE_OPTION)
       f = fc.getSelectedFile();
    return f;
    }

 private void openImage() {
    java.io.File f = showFileDialog(true);
    try {  
       sourceImage = ImageIO.read(f);
       JLabel l = new JLabel(new ImageIcon(sourceImage));
       originalPane.getViewport().add(l);
       this.validate();
       } catch(Exception ex) { ex.printStackTrace(); }
    }

 private void MessageAdd() {
    String mess = message.getText();
    embeddedImage = sourceImage.getSubimage(0,0,
       sourceImage.getWidth(),sourceImage.getHeight());
       
   MessageAdd(embeddedImage, mess);  // call MessageAdd  and initialize "embeddedImage" varible
   
    JLabel l = new JLabel(new ImageIcon(embeddedImage));
    embeddedPane.getViewport().add(l);
    this.validate();
    }

 private void MessageAdd(BufferedImage img, String mess) {
    int messageLength = mess.length();
   
    int imageWidth = img.getWidth(), imageHeight = img.getHeight(),
       imageSize = imageWidth * imageHeight;
       
    if(messageLength * 16 + 32 > imageSize) {
       JOptionPane.showMessageDialog(this, "Message is too long for the chosen image",
          "Message too long!", JOptionPane.ERROR_MESSAGE);
       return;
       }
       
       System.out.println("Message Length "+messageLength);  // see info
    
    embedInteger(img, messageLength, 0, 0); // set Length

    byte b[] = mess.getBytes(); 
    
    for(int i=0; i<b.length; i++)
       embedByte(img, b[i], i*16+32, 0);  // set Message
    }

 private void embedInteger(BufferedImage img, int n, int start, int storageBit) {
       int maxX = img.getWidth(), maxY = img.getHeight();
       int startX = start/maxY, startY = start - startX*maxY;
       int count=0;
       
       System.out.println("width "+maxX+" height "+maxY+" startX "+startX+" startY "+startY);
    
    for(int i=startX; i<maxX && count<32; i++) {
       for(int j=startY; j<maxY && count<32; j++) {
       	
          int rgb = img.getRGB(i, j);
          
          System.out.println("i "+i+" j "+j+" count "+count+" RGB "+rgb );   // rgb calulate
          
          int bit = getBitValue(n, count);
          rgb = setBitValue(rgb, storageBit, bit);
          img.setRGB(i, j, rgb);
          
          
          count++;
          }
       }
    }
    int even = 0;
     
 private void embedByte(BufferedImage img, byte b, int start, int storageBit) {
 	System.out.println("Byte b "+b+"\n \n");
    int maxX = img.getWidth(), maxY = img.getHeight();
    
    int startX = start/maxY, startY = start - startX*maxY, count=0; 
       
       System.out.print("width "+maxX+" height "+maxY+" startX "+startX+" startY "+startY);
      System.out.println(" startX "+startX+" startY "+startY);
  	int bb = b; 
  	System.out.println("bbbbbb "+bb);
  	
  	if(bb%2 == 0)
         bb+=2;
     else
         bb-=2;
  	
  	System.out.println("after even odd  "+bb);
      
    for(int i=startX; i<maxX && count<8; i++) {
       for(int j=startY; j<maxY && count<8; j++) {
          	
          
          
          	
          	if(even%2 == 0)
          	{
          		int rgb = img.getRGB(i, j);
          
         		 System.out.println("i "+i+" j "+j+" count "+count+" RGB "+rgb );   // rgb calulate
          
          
          		int bit = getBitValue(bb, count);
          		rgb = setBitValue(rgb, storageBit, bit);
          		img.setRGB(i, j, rgb);
          		count++;
          		even = 1;
          	} else {
          		even = 0;
          	}
          	
          	even = 0;
          }
       }
    }

 private void saveImage() {
    if(embeddedImage == null) {
       JOptionPane.showMessageDialog(this, "No message has been embedded!",
          "Nothing to save", JOptionPane.ERROR_MESSAGE);
       return;
       }
    java.io.File f = showFileDialog(false);
    String name = f.getName();
    String ext = name.substring(name.lastIndexOf(".")+1).toLowerCase();
    if(!ext.equals("png") && !ext.equals("bmp") &&   !ext.equals("dib")) {
          ext = "png";
          f = new java.io.File(f.getAbsolutePath()+".png");
          }
          
    try {
       if(f.exists()) f.delete();
       ImageIO.write(embeddedImage, ext.toUpperCase(), f);
       } catch(Exception ex) { ex.printStackTrace(); }
    }

 private void resetInterface() {
    message.setText("");
    originalPane.getViewport().removeAll();
    embeddedPane.getViewport().removeAll();
   sourceImage = null;
    embeddedImage = null;
    sp.setDividerLocation(0.5);
    this.validate();
    }

 private int getBitValue(int n, int location) {    //  getBit
    int v = n & (int) Math.round(Math.pow(2, location));
    
    System.out.println("get n "+n+" Location "+location+" V "+v);
    return v==0?0:1;
    }

 private int setBitValue(int n, int location, int bit) {   // setBit
    int toggle = (int) Math.pow(2, location);
    
    System.out.println("set n: "+n+" loc "+location);
    
    int bv = getBitValue(n, location);
     System.out.println("set bv: "+bv+" bit "+bit);
    
    if(bv == bit)
    {
    	System.out.println("Set n after condtion "+n+"\n");
    	return n;
    	
    }
       
    if(bv == 0 && bit == 1)
       n |= toggle;
    else if(bv == 1 && bit == 0)
       n ^= toggle;
       
     System.out.println("Set n after condtion "+n+"\n");  // Value decrease if GRB in minus value
    return n;
    }

 public static void main(String arg[]) {
    new MessageAdd();
    }
 }