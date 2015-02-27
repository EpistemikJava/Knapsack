/*
 * $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
  
  Mark Sattolo (epistemik@gmail.com)
 -----------------------------------------------
   $File: //depot/Eclipse/Java/workspace/KnapsackNew/src/mhs/knapsack/KnapLog.java $
   $Revision: #5 $
   $Change: 58 $
   $DateTime: 2011/02/02 11:56:15 $
   
  git version created Mar 22, 2014
  DrJava version created Feb 13, 2015
  
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ */

package mhs.knapsack;

import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.* ;
import java.io.IOException ;

/**
 * Manage logging for the package
 * @author  Mark Sattolo
 * @version $Revision: #5 $
 */
class KnapLogManager
{
  /**
   * USUAL Constructor <br>
   * Set up my Logger and Handler(s) and initiate logging at the startup Level
   * @param strLevel Starting Level
   * @see KnapLogger#getNewLogger
   * @see FileHandler
   * @see Handler#setFormatter
   */
  KnapLogManager( String strLevel )
  {
    // create a Knapsack Logger to log to a file
    myLogger = KnapLogger.getNewLogger( "Knapsack Logger" );
    if( myLogger == null )
    {
      System.err.println( "KnapLogManager CONSTRUCTOR: Could NOT get a Knapsack Logger??!!" );
      System.exit( this.hashCode() );
    }
    
    // get a Handler, set its formatter and add it to the Knapsack logger
    try
    {
      textHandler = new FileHandler( LOG_SUBFOLDER + KnapSack.PROJECT_NAME + LOG_ROLLOVER_SPEC + TEXT_LOGFILE_TYPE ,
                                     LOGFILE_MAX_BYTES, MAX_NUM_LOG_FILES );
      // set the Handler to use the Knapsack Formatter
      if( textHandler != null )
      {
        textHandler.setFormatter( new KnapFormatter() );
        
        // send logger output to our Handler
        myLogger.addHandler( textHandler );
      }
      else
          System.err.println( "KnapLogManager CONSTRUCTOR: textHandler is NULL!" );
    }
    catch( IOException ioe )
    {
      System.err.println( "KnapLogManager CONSTRUCTOR: Could NOT create a FileHandler: " + ioe );
    }
    catch( SecurityException se )
    {
      System.err.println( "KnapLogManager CONSTRUCTOR: Security Exception: " + se );
    }
    
    // get the anonymous logger -- it prints to console
    anonLogger = LogManager.getLogManager().getLogger("");
    if( anonLogger != null )
    {
      try
      {
        for( Handler h : anonLogger.getHandlers() )
          h.setFormatter( new KnapFormatter() );
      }
      catch( Exception e )
      {
        System.err.println( "KnapLogManager CONSTRUCTOR: anonLogger setFormatter() Exception: " + e );
      }
    }
    else
        System.err.println( "KnapLogManager CONSTRUCTOR: anonLogger is NULL!" );
    
    // Limit log messages to <strLevel> and above
    setLevel( strLevel );
    
  }// CONSTRUCTOR
  
  /** @return  private static {@link KnapLogger} <var>myLogger</var>  */
  protected KnapLogger getKnapLogger()
  { return myLogger ; }
  
  /** @return  private static {@link KnapLogger} <var>myLogger</var>  */
  protected Logger getAnonLogger()
  { return anonLogger ; }
  
  /** @return  private static {@link Level} <var>currentLevel</var>  */
  protected Level getLevel()
  { return currentLevel ;}
  
  /**
   * Set the level of detail that gets logged
   * @param lev - {@link java.lang.String}
   */
  void setLevel( String lev )
  {
    Level validLevel = DEFAULT_LEVEL ;
    if( lev != null && !lev.isEmpty() )
    {
      String s = lev.toUpperCase();
      try
      {
        validLevel = Level.parse( s );
      }
      catch( IllegalArgumentException iae )
      {
        System.err.println( "KnapLogManager.setLevel(String): " + iae.toString()
                            + " -- using DEFAULT Level: " + DEFAULT_LEVEL.getName() );
      }
    }
    else
        System.err.println( "KnapLogManager.setLevel(String): INVALID parameter -- using DEFAULT Level: "
                            + DEFAULT_LEVEL.getName() );
    
    setLevel( validLevel );
  }
  
  /**
   * Set the level of detail that gets logged
   * @param level - {@link java.util.logging.Level}
   */
  protected void setLevel( Level level )
  {
    currentLevel = level ;
    intLevel = currentLevel.intValue();
    
    if( myLogger != null )
    {
      myLogger.setLevel( currentLevel );
      for( Handler h: myLogger.getHandlers() )
        h.setLevel( level );
    }
    
    if( myLogger != null )
    {
      anonLogger.setLevel( currentLevel );
      for( Handler h: anonLogger.getHandlers() )
        h.setLevel( level );
    }
  }
  
  /**
   * @param level - {@link java.util.logging.Level}
   * @return  at this Level or more
   */
  protected static boolean atLevel( Level level )
  {
    return intLevel <= level.intValue();
  }
  
  /** @return EXACTLY at {@link Level#SEVERE}  */
  static boolean severe() { return currentLevel.equals( Level.SEVERE ); }
  /** @return at {@link Level#WARNING} or lower  */
  static boolean warning() { return atLevel( Level.WARNING ); }
  /** @return at {@link Level#CONFIG} or lower  */
  static boolean config() { return atLevel( Level.CONFIG ); }
  /** @return at {@link Level#INFO} or lower  */
  static boolean info()   { return atLevel( Level.INFO ); }
  /** @return at {@link Level#FINE} or lower  */
  static boolean fine()   { return atLevel( Level.FINE ); }
  /** @return at {@link Level#FINER} or lower  */
  static boolean finer()  { return atLevel( Level.FINER ); }
  /** @return at {@link Level#FINEST} or lower  */
  static boolean finest() { return atLevel( Level.FINEST ); }
  
  /**
   * Increase the amount of information logged <br>
   *  - which means we must decrease the {@link Level} of <var>myLogger</var> <br>
   *  - wrap around when reach base level   
   * @return {@link Level} <var>currentLevel</var>
   */
  protected Level moreLogging()
  {
    if( intLevel == Level.FINEST.intValue() )
      intLevel = Level.SEVERE.intValue(); // wrap around to HIGHEST (least amount of logging) setting
    else
      if( intLevel == Level.CONFIG.intValue() )
        intLevel = Level.FINE.intValue(); // jump gap b/n CONFIG & FINE 
      else
          intLevel -= 100 ; // go down to a finer (more logging) setting
    
    currentLevel = Level.parse( Integer.toString(intLevel) );
    setLevel( currentLevel );
    
    myLogger.severe( "Log level is NOW at " + currentLevel );
    
    return currentLevel ;
  
  }// LogControl.incLevel()
  
  String myname() { return getClass().getSimpleName(); }
  
  void reportLevel()
  {
    myLogger.severe( "Current log level is " + currentLevel );
  }
  
  void listLoggers()
  {
    LogManager lm = LogManager.getLogManager();
    myLogger.appendln( "Current Loggers:" );
    
    Enumeration<String> e = lm.getLoggerNames();
    while( e.hasMoreElements() )
    {
      String name = e.nextElement();
      myLogger.appendln( name );
      for( Handler h : lm.getLogger(name).getHandlers() )
        myLogger.append( "\t> " + h.getLevel() );
      myLogger.appendln( " |" );
    }
    
    myLogger.append( "=====================================" );
    myLogger.send( currentLevel );
  }
  
  void listActiveLoggers()
  {
    LogManager lm = LogManager.getLogManager();
    myLogger.appendln( "Active Loggers:" );
    
    Enumeration<String> e = lm.getLoggerNames();
    while( e.hasMoreElements() )
    {
      String name = e.nextElement();
      if( lm.getLogger(name).getHandlers().length > 0 )
      {
        myLogger.appendln( " * " + (name.equals("") ? "'anonymous logger'" : name) );
        for( Handler h : lm.getLogger(name).getHandlers() )
          myLogger.append( "     > " + h.getLevel() );
        myLogger.appendln( " |" );
      }
    }
    
    myLogger.append( "=====================================" );
    myLogger.send( currentLevel );
  }
  
  /** default value */
  static final int
                  MAX_NUM_LOG_FILES =   256 ,
                  LOGFILE_MAX_BYTES = ( 4 * 1024 * 1024 );
  
  /** default {@link Level} */
  static final Level
                    INIT_LEVEL = Level.SEVERE  , // Level to print initialization messages.
                 DEFAULT_LEVEL = Level.WARNING ; // If no value passed to Constructor from KnapSack.
  
  /** default Log name parameter */
  static final String
                     LOG_SUBFOLDER = "logs/" ,
                 LOG_ROLLOVER_SPEC = "%u-%g" ,
                  XML_LOGFILE_TYPE = ".xml"  ,
                 TEXT_LOGFILE_TYPE = ".log"  ;
  
  /** @see KnapLogger */
  private static KnapLogger myLogger ;
  
  /** @see FileHandler */
  private static FileHandler textHandler ;//, xmlHandler ;
  
  /** @see Logger */
  private static Logger anonLogger ;
  
  /** current {@link Level} */
  private static Level currentLevel ;
  
  /** integer value of {@link #currentLevel} */
  private static int intLevel ;

}/* class KnapLogManager */

/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */

/**
 *  Perform all the actual logging operations
 *  @author Mark Sattolo
 *  @see java.util.logging.Logger
 */
class KnapLogger extends Logger
{
 /*            C O N S T R U C T O R S
  *****************************************************************************************************/
   
  /**
   * USUAL constructor - just calls the super equivalent
   * @param name - may be <var>null</var>
   * @param resourceBundleName - may be <var>null</var>
   * @see Logger#Logger(String,String)
   */
  private KnapLogger( String name, String resourceBundleName )
  {
    super( name, resourceBundleName );
  }
  
 /*              M E T H O D S
  ***************************************************************************************************/
  
 // =============================================================================
 //                          I N T E R F A C E
 // =============================================================================
   
  /**
   * Allow other package classes to create a {@link Logger} <br>
   * - adds this new {@link Logger} to the {@link LogManager} namespace
   * @param name - identify the {@link Logger}
   * @return the <b>new</b> {@link Logger}
   * @see LogManager#addLogger(Logger)
   */
  protected static synchronized KnapLogger getNewLogger( String name )
  {
    KnapLogger mylogger = new KnapLogger( name, null );
    LogManager.getLogManager().addLogger( mylogger );
    return mylogger ;
    
  }// KnapLogger.getNewLogger()
  
  /**
   * Prepare and send a {@link LogRecord} with data from the log buffer
   * @param level - {@link Level} to log at
   */
  protected void send( Level level )
  {
    if( buffer.length() == 0 )
      return ;
    
    getCallerClassAndMethodName();
    LogRecord lr = getRecord( level, buffer.toString() );
    clean();
    
    sendRecord( lr );
  
  }// KnapLogger.send()
  
  /**
   * Add data to the log buffer
   * @param msg - data String 
   */
  protected synchronized void append( String msg )
  { buffer.append( msg ); }
  
  /** 
   * Add data to the log buffer with a terminating newline
   * @param msg - data String 
   */
  protected void appendln( String msg )
  { append( msg + "\n" ); }
  
  /** Add a newline to the log buffer  */
  protected void appendnl()
  { append( "\n" ); }
  
  /** <b>Remove</b> <em>ALL</em> data in the log buffer  */
  protected void clean()
  { buffer.delete( 0, buffer.length() ); }
  
  /**
   * Log the submitted info at the current level
   * @param s - info to print
   */
  public void log( String s )
  {
    log( KnapSack.currentLevel, s );
  }

  /*/ for debugging  
  @Override
  public void log( LogRecord record )
  {
    System.out.println( "---------------------------------------------------" );
    System.out.println( "record Message is '" + record.getMessage() + "'" );
    System.out.println( "record Class caller is '" + record.getSourceClassName() + "'" );
    System.out.println( "record Method caller is '" + record.getSourceMethodName() + "'" );
    super.log( record );
  }
  //*/  
  
 // =============================================================================
 //                            P R I V A T E
 // =============================================================================
  
  /**
   * Provide a <b>new</b> {@link LogRecord} with Caller class and method name info
   * @param level - {@link Level} to log at
   * @param msg - info to insert in the {@link LogRecord}
   * @return the produced {@link LogRecord}
   */
  private LogRecord getRecord( Level level, String msg )
  {
    LogRecord lr = new LogRecord( (level == null ? KnapLogManager.DEFAULT_LEVEL : level), msg );
    lr.setSourceClassName( callclass );
    lr.setSourceMethodName( callmethod );
    return lr ;
    
  }// KnapLogger.getRecord()
  
  /**
   *  Actually send the {@link LogRecord} to the logging handler
   *  @param lr - {@link LogRecord} to send
   *  @see Logger#log(LogRecord)
   */
  private synchronized void sendRecord( LogRecord lr )
  {
    callclass  = null ;
    callmethod = null ;
    
    super.log( lr );
    
  }// KnapLogger.sendRecord()
  
  /**
   *  Get the name of the {@link Class} and <em>Method</em> that called {@link KnapLogger}
   *  @see Throwable#getStackTrace
   *  @see StackTraceElement#getClassName
   *  @see StackTraceElement#getMethodName
   */
  private void getCallerClassAndMethodName()
  {
    Throwable t = new Throwable();
    StackTraceElement[] elements = t.getStackTrace();
    
    if( elements.length < 3 )
      callclass = callmethod = strUNKNOWN ;
    else
    {
      callclass = elements[2].getClassName();
      callmethod = elements[2].getMethodName();
    }
  
  }// KnapLogger.getCallerClassAndMethodName()
  
 /*            F I E L D S
  ***************************************************************************************************/
  
  /** Class calling the Logger */
  private String callclass = null ;
  /** Method calling the Logger */
  private String callmethod = null ;
  
  /**
   *  Store info from multiple {@link KnapLogger#append} or {@link KnapLogger#appendln} calls <br>
   *  - i.e. do a 'bulk send'
   *  @see StringBuilder
   */
  private StringBuilder buffer = new StringBuilder( 1024 );
  
  /** default if cannot get method or class name  */
  static final String strUNKNOWN = "unknown" ;
  
}/* class KnapLogger */

/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */

/**
 *  Do all the actual formatting of {@link LogRecord}s for {@link KnapLogger}
 *  @author Mark Sattolo
 *  @see java.util.logging.Formatter
 */
class KnapFormatter extends Formatter
{
  /**
   *  Instructions on how to format a {@link LogRecord}
   *  @see Formatter#format
   */
  @Override
  public String format( LogRecord record )
  {
    return( record.getLevel() + rec + (++count) + nl + record.getSourceClassName() + sp
            + record.getSourceMethodName() + mi + nl + record.getMessage() + nl + nl         );
  }
  
  /**
   *  Printed at the beginning of a Log file
   *  @see Formatter#getHead
   */
  @Override
  public String getHead( Handler h )
  {
    return( head + DateFormat.getDateTimeInstance().format( new Date() ) + nl + div + nl + nl );
  }
  
  /**
   *  Printed at the end of a Log file
   *  @see Formatter#getTail
   */
  @Override
  public String getTail( Handler h )
  {
    return( div + nl + tail + DateFormat.getDateTimeInstance().format( new Date() ) + nl );
  }
  
  /** Number of times {@link KnapFormatter#format(LogRecord)} has been called  */
  private int count ;
  
  /** useful String constant */
  static String sp   = " "  ,
                nl   = "\n" ,
                mi   = "()" , // method indicator
                div  = "=================================================================" ,
                head = "Knapsack START" + nl ,
                rec  = ": Knapsack Record #" ,
                tail = "Knapsack END" + nl   ;

}/* class KnapFormatter */
