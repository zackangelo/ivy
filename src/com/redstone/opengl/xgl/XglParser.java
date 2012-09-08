package com.redstone.opengl.xgl;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.*;
import java.nio.FloatBuffer;

import java.util.*;

class XglHandler extends DefaultHandler {
	public XglHandler() { 
//		tagMap = buildTagMap();
		tagStack = new Stack<XglTag>();
		chars = new StringBuilder();
		objectStack = new Stack<XglObject>();
	}
	
	private XglWorld xglWorld;
	
	//current state variables
	private XglMesh xglMesh;
	private XglVertex point;
	private XglNormal xglNormal;
	private XglFace face;
	private XglMaterial xglMaterial;
	private int faceVertexIndex;
	private XglTransform xglTransform;
	
	private XglObject object;
	
	enum XglTag { 
		P,N,PREF,NREF,MESH,/*FACE,*/VERTEX,OBJECT,FV1,FV2,FV3,MESHREF,PATCH,
		AMB,DIFF,SPEC,EMISS,SHINE,ALPHA,MAT,MATREF,TRANSFORM,FORWARD,UP,POSITION,
		WORLD,DATA,STR,BACKGROUND,BACKCOLOR,LIGHTING,DIRECTIONALLIGHT,DIFFUSE,SPECULAR,AMBIENT,DIRECTION,
		CENTER,SPHEREMAP,LINESTYLE,POINTSTYLE,TEXTURE,TEXTURERGB,TEXTURERGBA,TC,TCREF,RADIUS,
		INCLUDE,INCLUDESTATIC,MODULATE,REPEAT,F,TEXTUREREF,SURFACE,extCOMMENT
	}
	
	private Stack<XglObject> objectStack;
	private Stack<XglTag> tagStack; 
	private XglTag tag;
//	private Map<String,XglTag> tagMap;
	private StringBuilder chars;
	
//	private Map<String,XglTag> buildTagMap() { 
//		Map<String,XglTag> tagMap = new HashMap<String,XglTag>();
//		
//		tagMap.put("P",XglTag.P);
//		tagMap.put("N",XglTag.N);
//		tagMap.put("PREF",XglTag.PREF);
//		tagMap.put("NREF",XglTag.NREF);
//		tagMap.put("MESH",XglTag.MESH);
//		tagMap.put("F",XglTag.F);
//		tagMap.put("VERTEX",XglTag.VERTEX);
//		tagMap.put("OBJECT",XglTag.OBJECT);
//		tagMap.put("FV1",XglTag.FV1);
//		tagMap.put("FV2",XglTag.FV2);
//		tagMap.put("FV3",XglTag.FV3);
//		tagMap.put("MESHREF",XglTag.MESHREF);
//		tagMap.put("PATCH",XglTag.PATCH);
//		tagMap.put("AMB",XglTag.AMB);
//		tagMap.put("DIFF",XglTag.DIFF);
//		tagMap.put("SPEC",XglTag.SPEC);
//		tagMap.put("EMISS",XglTag.EMISS);
//		tagMap.put("ALPHA",XglTag.ALPHA); 
//		tagMap.put("SHINE",XglTag.SHINE);
//		tagMap.put("MAT",XglTag.MAT);
//		tagMap.put("MATREF",XglTag.MATREF);
//		tagMap.put("TRANSFORM",XglTag.TRANSFORM);
//		tagMap.put("FORWARD",XglTag.FORWARD);
//		tagMap.put("UP",XglTag.UP);
//		tagMap.put("POSITION",XglTag.POSITION);
//		
//		return tagMap;
//	}
	
	
	StringBuilder tripParse = new StringBuilder();
	
	private FloatBuffer parseTripletToFloatBuffer(String t) { 
		tripParse.setLength(0);
		
		float[] a = new float[4];
		int i = 0,ai = 0;
		while(i < t.length()) {
			char c = t.charAt(i++);
			
			if(c == ',') {
				a[ai++] = Float.parseFloat(tripParse.toString());
				tripParse.setLength(0);
			} else {
				tripParse.append(c);
			}
		}
		
		a[ai++] = Float.parseFloat(tripParse.toString());
		
		FloatBuffer fb = BufferUtils.createFloatBuffer(a.length);
		
		fb.put(a);
		fb.rewind();
		
//		System.out.println("Parsed: " + Arrays.toString(a));
		
		return fb;
	}
	private float[] parseTripletToFloats(String t) { 
//		String[] ta = t.split(",");
//		float[] ra = new float[ta.length];
//		for(int i=0;i<ta.length;i++)
//			ra[i] = Float.parseFloat(ta[i]);
//		
//		return ra;
		
		tripParse.setLength(0);
		
		float[] a = new float[4];
		int i = 0,ai = 0;
		while(i < t.length()) {
			char c = t.charAt(i++);
			
			if(c == ',') {
				a[ai++] = Float.parseFloat(tripParse.toString());
				tripParse.setLength(0);
			} else {
				tripParse.append(c);
			}
		}
		
		a[ai++] = Float.parseFloat(tripParse.toString());
		
//		System.out.println("Parsed: " + Arrays.toString(a));
		
		return a;
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		chars.append(ch,start,length);
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}



	@Override
	public void startDocument() throws SAXException {
		xglWorld = new XglWorld();
		tag = null;
		objectStack.setSize(0);	//clear object stack
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
//		if(tag != null) { 
//			tagStack.push(tag);
//		}
		
		//reset charbuffer
		chars.setLength(0);			
		
		tag = tagStack.push(Enum.valueOf(XglTag.class, localName));
		
		if(tag==null) return;
		switch(tag) { 
		case MESH: xglMesh = new XglMesh(attributes.getValue("ID"));  break;
		case P: point = new XglVertex(Integer.parseInt(attributes.getValue("ID")));  break;
		case N: xglNormal = new XglNormal(Integer.parseInt(attributes.getValue("ID")));  break;
		case OBJECT: 
			if(object != null) 
				objectStack.push(object);
			
			object = new XglObject((objectStack.empty()?null:objectStack.peek()));  
			break;
		case F: face = new XglFace(); xglMesh.addFace(face); break;
		case FV1: faceVertexIndex = 0; break;
		case FV2: faceVertexIndex = 1; break;
		case FV3: faceVertexIndex = 2; break;
		case MAT: xglMaterial = new XglMaterial(); xglMesh.addMaterial(Integer.parseInt(attributes.getValue("ID")), xglMaterial); break;
		case TRANSFORM: xglTransform = new XglTransform();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(!tagStack.empty()) {
			tag = tagStack.pop();
		} else {
			tag = null;
		}
		
		if(tag==null) return;
		
//		System.out.println("tag = " + tag.name() + ", localName = " + localName);
		float[] v;
		
		switch(tag) { 
		case MESH: 
			if(xglMesh == null) { 
				throw new SAXException("End mesh tag with no mesh object! (" + localName + ")"); 
			} 
			
			xglWorld.addMesh(xglMesh); 
			xglMesh = null; 
			break;
		case P: point.setValues(parseTripletToFloats(chars.toString())); xglMesh.addPoint(point); point = null; break;
		case N: xglNormal.setValues(parseTripletToFloats(chars.toString())); xglMesh.addNormal(xglNormal); xglNormal = null; break;
		case PREF: 
			if(face!= null) { 
				face.v[faceVertexIndex] = xglMesh.getPoint(Integer.parseInt(chars.toString())); 
			}
			break;
		case NREF: 
			if(face != null) { 
				face.n[faceVertexIndex] = xglMesh.getNormal(Integer.parseInt(chars.toString()));
			}
			break;
		case F: /*mesh.addFace(face);*/ face = null;  break;
		case MESHREF: object.setMesh(xglWorld.getMesh(chars.toString())); break;
		case OBJECT: 
			if(objectStack.empty()) {
				xglWorld.addObject(object); 
				object = null; 
			} else {
				objectStack.peek().addSubObject(object);
				object = objectStack.pop();
			}
		break;
		case AMB: xglMaterial.ambient = 
				parseTripletToFloatBuffer(chars.toString() + ",1.0");
				break;
		case DIFF: xglMaterial.diffuse = parseTripletToFloatBuffer(chars.toString() + ",1.0"); break;
		case SPEC: xglMaterial.specular = parseTripletToFloatBuffer(chars.toString() + ",1.0"); break;
		case SHINE: xglMaterial.shininess = Float.parseFloat(chars.toString()); break;
		case EMISS: xglMaterial.emissive = parseTripletToFloatBuffer(chars.toString()); break;
		case MAT: xglMaterial = null; break;
		case MATREF: face.material = xglMesh.getMaterial(Integer.parseInt(chars.toString())); break;
		case TRANSFORM:
			object.setTransform(xglTransform); 
			break;
		case FORWARD: 
			v = parseTripletToFloats(chars.toString());
			xglTransform.setForward(new Vector3f(v[0],v[1],v[2])); 
			break;
		case UP: 
			v = parseTripletToFloats(chars.toString());
			xglTransform.setUp(new Vector3f(v[0],v[1],v[2])); 
			break;
		case POSITION: 
			v = parseTripletToFloats(chars.toString());
			xglTransform.setPosition(new Vector3f(v[0],v[1],v[2])); 
			break;
		}
		
		
	}
	
	public XglWorld getWorld() { 
		return xglWorld;
	}
	
	Locator locator;

	@Override
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}
	
	public String getLocation() { 
		return "Line: " + locator.getLineNumber() + ", " + " Col: " + locator.getColumnNumber();
	}
	
}

public class XglParser {

	public XglWorld parse(InputStream stream) throws XglException { 
		XMLReader xmlReader;
		XglHandler handler = new XglHandler();
		
		try {
			xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(handler);
			xmlReader.setErrorHandler(handler);
//			xmlReader.setDTDHandler(handler);
			xmlReader.parse(new InputSource(stream));
			return handler.getWorld();
		} catch (Throwable e) { 
			throw new XglException("Error parsing XGL document. ("+handler.getLocation()+")",e);
		}
	}
	
	public XglWorld parse(String fileName) throws FileNotFoundException, XglException { 
		InputStream is = new BufferedInputStream(new FileInputStream(fileName));
		return parse(is);
	}
}
