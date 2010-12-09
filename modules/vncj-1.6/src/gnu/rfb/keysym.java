package gnu.rfb;

/**
* <br><br><center><table border="1" width="80%"><hr>
* <strong><a href="http://www.amherst.edu/~tliron/vncj">VNCj</a></strong>
* <p>
* Copyright (C) 2000-2002 by Tal Liron
* <p>
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public License
* as published by the Free Software Foundation; either version 2.1
* of the License, or (at your option) any later version.
* <p>
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* <a href="http://www.gnu.org/copyleft/lesser.html">GNU Lesser General Public License</a>
* for more details.
* <p>
* You should have received a copy of the <a href="http://www.gnu.org/copyleft/lesser.html">
* GNU Lesser General Public License</a> along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
* <hr></table></center>
**/

import java.awt.event.*;

public abstract class keysym
{
	/*
	 *  the keysym values according to:
	 *  	http://cgit.freedesktop.org/xorg/proto/x11proto/plain/keysymdef.h
	 */
	
	public static final int DeadGrave		= 0xFE50;
	public static final int DeadAcute		= 0xFE51;
	public static final int DeadCircumflex	= 0xFE52;
	public static final int DeadTilde		= 0xFE53;
	
	public static final int BackSpace	= 0xFF08;
	public static final int Tab			= 0xFF09;
	public static final int Linefeed	= 0xFF0A;
	public static final int Clear		= 0xFF0B;
	public static final int Return		= 0xFF0D;
	public static final int Pause		= 0xFF13;
	public static final int ScrollLock	= 0xFF14;
	public static final int SysReq		= 0xFF15;
	public static final int Escape		= 0xFF1B;
	
	public static final int Delete		= 0xFFFF;
	
	public static final int Home		= 0xFF50;
	public static final int Left		= 0xFF51;
	public static final int Up			= 0xFF52;
	public static final int Right		= 0xFF53;
	public static final int Down		= 0xFF54;
	public static final int PageUp		= 0xFF55;
	public static final int PageDown	= 0xFF56;
	public static final int End			= 0xFF57;
	public static final int Begin		= 0xFF58;
	
	public static final int Select		= 0xFF60;
	public static final int Print		= 0xFF61;
	public static final int Execute		= 0xFF62;
	public static final int Insert		= 0xFF63;
	
	public static final int Cancel		= 0xFF69;
	public static final int Help		= 0xFF6A;
	public static final int Break		= 0xFF6B;
	public static final int NumLock		= 0xFF6F;
	
	public static final int KpSpace		= 0xFF80;
	public static final int KpTab		= 0xFF89;
	public static final int KpEnter		= 0xFF8D;

	public static final int KpHome		= 0xFF95;
	public static final int KpLeft		= 0xFF96;
	public static final int KpUp		= 0xFF97;
	public static final int KpRight		= 0xFF98;
	public static final int KpDown		= 0xFF99;
	public static final int KpPrior		= 0xFF9A;
	public static final int KpPageUp	= 0xFF9A;
	public static final int KpNext		= 0xFF9B;
	public static final int KpPageDown	= 0xFF9B;
	public static final int KpEnd		= 0xFF9C;
	public static final int KpBegin		= 0xFF9D;
	public static final int KpInsert	= 0xFF9E;
	public static final int KpDelete	= 0xFF9F;
	public static final int KpEqual		= 0xFFBD;
	public static final int KpMultiply	= 0xFFAA;
	public static final int KpAdd		= 0xFFAB;
	public static final int KpSeparator	= 0xFFAC;	
	public static final int KpSubtract	= 0xFFAD;
	public static final int KpDecimal	= 0xFFAE;
	public static final int KpDivide	= 0xFFAF;
	
	public static final int KpF1		= 0xFF91;
	public static final int KpF2		= 0xFF92;
	public static final int KpF3		= 0xFF93;
	public static final int KpF4		= 0xFF94;

	public static final int Kp0			= 0xFFB0;
	public static final int Kp1			= 0xFFB1;
	public static final int Kp2			= 0xFFB2;
	public static final int Kp3			= 0xFFB3;
	public static final int Kp4			= 0xFFB4;
	public static final int Kp5			= 0xFFB5;
	public static final int Kp6			= 0xFFB6;
	public static final int Kp7			= 0xFFB7;
	public static final int Kp8			= 0xFFB8;
	public static final int Kp9			= 0xFFB9;
	
	public static final int F1			= 0xFFBE;
	public static final int F2			= 0xFFBF;
	public static final int F3			= 0xFFC0;
	public static final int F4			= 0xFFC1;
	public static final int F5			= 0xFFC2;
	public static final int F6			= 0xFFC3;
	public static final int F7			= 0xFFC4;
	public static final int F8			= 0xFFC5;
	public static final int F9			= 0xFFC6;
	public static final int F10			= 0xFFC7;
	public static final int F11			= 0xFFC8;
	public static final int F12			= 0xFFC9;
	public static final int F13			= 0xFFCA;
	public static final int F14			= 0xFFCB;
	public static final int F15			= 0xFFCC;
	public static final int F16			= 0xFFCD;
	public static final int F17			= 0xFFCE;
	public static final int F18			= 0xFFCF;
	public static final int F19			= 0xFFD0;
	public static final int F20			= 0xFFD1;
	public static final int F21			= 0xFFD2;
	public static final int F22			= 0xFFD3;
	public static final int F23			= 0xFFD4;
	public static final int F24			= 0xFFD5;
	
	// meta keys
	public static final int ShiftL		= 0xFFE1;
	public static final int ShiftR		= 0xFFE2;
	public static final int ControlL	= 0xFFE3;
	public static final int ControlR	= 0xFFE4;
	public static final int CapsLock	= 0xFFE5;
	public static final int ShiftLock	= 0xFFE6;
	public static final int MetaL		= 0xFFE7;
	public static final int MetaR		= 0xFFE8;
	public static final int AltL		= 0xFFE9;
	public static final int AltR		= 0xFFEA;

	// the numbers - just give the value back?
	public static final int Num0		= 0x0030;
	public static final int Num1		= 0x0031;
	public static final int Num2		= 0x0032;
	public static final int Num3		= 0x0033;
	public static final int Num4		= 0x0034;
	public static final int Num5		= 0x0035;
	public static final int Num6		= 0x0036;
	public static final int Num7		= 0x0037;
	public static final int Num8		= 0x0038;
	public static final int Num9		= 0x0039;

	// do not or do calculate them? (0x0061-0x0020)
	public static final int CharA		= 0x0061;
	public static final int CharB		= 0x0062;
	public static final int CharC		= 0x0063;
	public static final int CharD		= 0x0064;
	public static final int CharE		= 0x0065;
	public static final int CharF		= 0x0066;
	public static final int CharG		= 0x0067;
	public static final int CharH		= 0x0068;
	public static final int CharI		= 0x0069;
	public static final int CharJ		= 0x006A;
	public static final int CharK		= 0x006B;
	public static final int CharL		= 0x006C;
	public static final int CharM		= 0x006D;
	public static final int CharN		= 0x006E;
	public static final int CharO		= 0x006F;
	public static final int CharP		= 0x0070;
	public static final int CharQ		= 0x0071;
	public static final int CharR		= 0x0072;
	public static final int CharS		= 0x0073;
	public static final int CharT		= 0x0074;
	public static final int CharU		= 0x0075;
	public static final int CharV		= 0x0076;
	public static final int CharW		= 0x0077;
	public static final int CharX		= 0x0078;
	public static final int CharY		= 0x0079;
	public static final int CharZ		= 0x007A;
	
	
	public static int toVK( int keysym )
	{
		/*
		 * at the time a special treating only for
		 * 		Latin 1
		 * the most char keys
		 * going through the number keys (0-9)
		 * and through the capital letters (A-Z)
		 */
		if (	((keysym >= 0x0020) & (keysym <= 0x0060)) || 
				((keysym >= 0x007B) & (keysym <= 0x007E)) ||
				((keysym >= 0x00A0) & (keysym <= 0x00FF))
					) return keysym;
		/*
		 * the small letter should be interpreted as their "big relatives"
		 */
		else if((keysym >= 0x0061) & (keysym <= 0x007A)) return (keysym - 0x0020); 
		else
		{
			switch( keysym )
			{
			case DeadGrave: return KeyEvent.VK_DEAD_GRAVE;
			case DeadAcute: return KeyEvent.VK_DEAD_ACUTE;
			case DeadCircumflex: return KeyEvent.VK_DEAD_CIRCUMFLEX;
			case DeadTilde: return KeyEvent.VK_DEAD_TILDE;
			
			case BackSpace: return KeyEvent.VK_BACK_SPACE;
			case Tab: return KeyEvent.VK_TAB;
			//No Java equivalent: case Linefeed: return KeyEvent.;
			case Clear: return KeyEvent.VK_CLEAR;
			case Return: return KeyEvent.VK_ENTER;
			case Pause: return KeyEvent.VK_PAUSE;
			case ScrollLock: return KeyEvent.VK_SCROLL_LOCK;
			//No Java equivalent: case SysReq: return KeyEvent.;
			case Escape: return KeyEvent.VK_ESCAPE;
			
			case Delete: return KeyEvent.VK_DELETE;
			
			case Home: return KeyEvent.VK_HOME;
			case Left: return KeyEvent.VK_LEFT;
			case Up: return KeyEvent.VK_UP;
			case Right: return KeyEvent.VK_RIGHT;
			case Down: return KeyEvent.VK_DOWN;
			case PageUp: return KeyEvent.VK_PAGE_UP;
			case PageDown: return KeyEvent.VK_PAGE_DOWN;
			case End: return KeyEvent.VK_END;
			//No Java equivalent: case Begin: return KeyEvent.;
			
			//No Java equivalent: case Select: return KeyEvent.;
			case Print: return KeyEvent.VK_PRINTSCREEN;
			//No Java equivalent: case Execute: return KeyEvent.;
			case Insert: return KeyEvent.VK_INSERT;
			
			case Cancel: return KeyEvent.VK_CANCEL;
			case Help: return KeyEvent.VK_HELP;
			//No Java equivalent: case Break: return KeyEvent.;
			case NumLock: return KeyEvent.VK_NUM_LOCK;
			
			case KpSpace: return KeyEvent.VK_SPACE;
			case KpTab: return KeyEvent.VK_TAB;
			case KpEnter: return KeyEvent.VK_ENTER;
			
			case KpHome: return KeyEvent.VK_HOME;
			case KpLeft: return KeyEvent.VK_LEFT;
			case KpUp: return KeyEvent.VK_UP;
			case KpRight: return KeyEvent.VK_RIGHT;
			case KpDown: return KeyEvent.VK_DOWN;
			case KpPageUp: return KeyEvent.VK_PAGE_UP; // = KpPrior
			case KpPageDown: return KeyEvent.VK_PAGE_DOWN; // = KpNext
			case KpEnd: return KeyEvent.VK_END;
			//No Java equivalent: case KpBegin: return KeyEvent.;
			case KpInsert: return KeyEvent.VK_INSERT;
			case KpDelete: return KeyEvent.VK_DELETE;
			case KpEqual: return KeyEvent.VK_EQUALS;
			case KpMultiply: return KeyEvent.VK_MULTIPLY;
			case KpAdd: return KeyEvent.VK_ADD;
			case KpSeparator: return KeyEvent.VK_SEPARATER; // Sun should spellcheck...
			case KpSubtract: return KeyEvent.VK_SUBTRACT;
			case KpDecimal: return KeyEvent.VK_DECIMAL;
			case KpDivide: return KeyEvent.VK_DIVIDE;
			
			case KpF1: return KeyEvent.VK_F1;
			case KpF2: return KeyEvent.VK_F2;
			case KpF3: return KeyEvent.VK_F3;
			case KpF4: return KeyEvent.VK_F4;
			
			case Kp0: return KeyEvent.VK_NUMPAD0;
			case Kp1: return KeyEvent.VK_NUMPAD1;
			case Kp2: return KeyEvent.VK_NUMPAD2;
			case Kp3: return KeyEvent.VK_NUMPAD3;
			case Kp4: return KeyEvent.VK_NUMPAD4;
			case Kp5: return KeyEvent.VK_NUMPAD5;
			case Kp6: return KeyEvent.VK_NUMPAD6;
			case Kp7: return KeyEvent.VK_NUMPAD7;
			case Kp8: return KeyEvent.VK_NUMPAD8;
			case Kp9: return KeyEvent.VK_NUMPAD9;
			
			case F1: return KeyEvent.VK_F1;
			case F2: return KeyEvent.VK_F2;
			case F3: return KeyEvent.VK_F3;
			case F4: return KeyEvent.VK_F4;
			case F5: return KeyEvent.VK_F5;
			case F6: return KeyEvent.VK_F6;
			case F7: return KeyEvent.VK_F7;
			case F8: return KeyEvent.VK_F8;
			case F9: return KeyEvent.VK_F9;
			case F10: return KeyEvent.VK_F10;
			case F11: return KeyEvent.VK_F11;
			case F12: return KeyEvent.VK_F12;
			case F13: return KeyEvent.VK_F12;
			case F14: return KeyEvent.VK_F12;
			case F15: return KeyEvent.VK_F12;
			case F16: return KeyEvent.VK_F12;
			case F17: return KeyEvent.VK_F12;
			case F18: return KeyEvent.VK_F12;
			case F19: return KeyEvent.VK_F12;
			case F20: return KeyEvent.VK_F12;
			case F21: return KeyEvent.VK_F12;
			case F22: return KeyEvent.VK_F12;
			case F23: return KeyEvent.VK_F12;
			case F24: return KeyEvent.VK_F12;
	
			case ShiftL: return KeyEvent.VK_SHIFT;
			case ShiftR: return KeyEvent.VK_SHIFT;
			case ControlL: return KeyEvent.VK_CONTROL;
			case ControlR: return KeyEvent.VK_CONTROL;
			case MetaL: return KeyEvent.VK_META;
			case MetaR: return KeyEvent.VK_META;
			case AltL: return KeyEvent.VK_ALT;
			case AltR: return KeyEvent.VK_ALT_GRAPH;
	
			case CapsLock: return KeyEvent.VK_CAPS_LOCK;
			//No Java equivalent: case ShiftLock: return KeyEvent.;
			default: return 0;
			}
		}
	}

	public static int toVKall( int keysym )
	{
		int key = toVK( keysym );
		if( key != 0 )
			return key;
		
		switch( keysym )
		{
		case ShiftL: return KeyEvent.VK_SHIFT;
		case ShiftR: return KeyEvent.VK_SHIFT;
		case ControlL: return KeyEvent.VK_CONTROL;
		case ControlR: return KeyEvent.VK_CONTROL;
		case MetaL: return KeyEvent.VK_META;
		case MetaR: return KeyEvent.VK_META;
		case AltL: return KeyEvent.VK_ALT;
		case AltR: return KeyEvent.VK_ALT_GRAPH;
		default: return 0;
		}
	}
	
	public static int toMask( int keysym )
	{
		switch( keysym )
		{
		case ShiftL: return KeyEvent.SHIFT_MASK;
		case ShiftR: return KeyEvent.SHIFT_MASK;
		case ControlL: return KeyEvent.CTRL_MASK;
		case ControlR: return KeyEvent.CTRL_MASK;
		case MetaL: return KeyEvent.META_MASK;
		case MetaR: return KeyEvent.META_MASK;
		case AltL: return KeyEvent.ALT_MASK;
		case AltR: return KeyEvent.ALT_GRAPH_MASK;
		default: return 0;
		}
	}
	/*
	 * won't be needed, because the mask keys also generate events
	 */
	public static int maskToVKey( int mask )
	{
		switch( mask )
		{
		case KeyEvent.SHIFT_MASK: return KeyEvent.VK_SHIFT;
		case KeyEvent.CTRL_MASK: return KeyEvent.VK_CONTROL;
		case KeyEvent.META_MASK: return KeyEvent.VK_META;
		case KeyEvent.ALT_MASK: return KeyEvent.VK_ALT;
		default: return 0;
		}
	}
}
