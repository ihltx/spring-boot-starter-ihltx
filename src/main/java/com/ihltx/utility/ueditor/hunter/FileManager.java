package com.ihltx.utility.ueditor.hunter;


import com.ihltx.utility.ueditor.PathFormat;
import com.ihltx.utility.ueditor.define.AppInfo;
import com.ihltx.utility.ueditor.define.BaseState;
import com.ihltx.utility.ueditor.define.MultiState;
import com.ihltx.utility.ueditor.define.State;
import com.ihltx.utility.util.StringUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class FileManager {

	private String dir = null;
	private String rootPath = null;
	private String[] allowFiles = null;
	private int count = 0;
	private Map<String, Object> conf = null;

	public FileManager ( Map<String, Object> conf ) {
		this.conf = conf;
		this.rootPath = (String)conf.get( "rootPath" );
		if(conf.get("physicsPath")==null || StringUtil.isNullOrEmpty(conf.get("physicsPath").toString())){
			this.dir = this.rootPath + (String)conf.get( "dir" );
		}else{
			this.dir = (String) conf.get("physicsPath") + (String)conf.get( "dir" );
		}
		this.allowFiles = this.getAllowFiles( conf.get("allowFiles") );
		this.count = (Integer)conf.get( "count" );
		
	}
	
	public State listFile (int index ) {
		
		File dir = new File( this.dir );
		State state = null;

		if ( !dir.exists() ) {
			return new BaseState( false, AppInfo.NOT_EXIST );
		}
		
		if ( !dir.isDirectory() ) {
			return new BaseState( false, AppInfo.NOT_DIRECTORY );
		}
		
		Collection<File> list = FileUtils.listFiles( dir, this.allowFiles, true );
		if ( index < 0 || index > list.size() ) {
			state = new MultiState( true );
		} else {
			Object[] fileList = Arrays.copyOfRange( list.toArray(), index, index + this.count );
			state = this.getState( fileList );
		}
		
		state.putInfo( "start", index );
		state.putInfo( "total", list.size() );
		
		return state;
		
	}
	
	private State getState ( Object[] files ) {
		
		MultiState state = new MultiState( true );
		BaseState fileState = null;
		
		File file = null;
		
		for ( Object obj : files ) {
			if ( obj == null ) {
				break;
			}
			file = (File)obj;
			fileState = new BaseState( true );
			fileState.putInfo( "url", PathFormat.format( this.getPath( file ) ) );
			state.addState( fileState );
		}
		
		return state;
		
	}
	
	private String getPath ( File file ) {
		
		String path = file.getAbsolutePath().replaceAll("\\\\" ,"/");
		if(conf.get("physicsPath")!=null && !StringUtil.isNullOrEmpty(conf.get("physicsPath").toString())){
			path = path.replaceAll(conf.get("physicsPath").toString(),"");
		}else{
			path = path.replaceAll(this.rootPath.replaceAll("\\\\","/"),"/");
		}
		return path;
		
	}
	
	private String[] getAllowFiles ( Object fileExt ) {
		
		String[] exts = null;
		String ext = null;
		
		if ( fileExt == null ) {
			return new String[ 0 ];
		}
		
		exts = (String[])fileExt;
		
		for ( int i = 0, len = exts.length; i < len; i++ ) {
			
			ext = exts[ i ];
			exts[ i ] = ext.replace( ".", "" );
			
		}
		
		return exts;
		
	}
	
}
