package com.gree.gprs.util.lzo;

public class LzoCompressor1x_1 extends AbstractLzo1Compressor {

	public LzoCompressor1x_1() {
		super(LzoAlgorithm.LZO1X, null);
	}

	public int getCompressionLevel() {
		return 5;
	}

	public String toString() {
		return "LZO1X1";
	}

	// In Java, all of these are array indices.

	// for lzo1y.h and lzo1z.h

	// Unfortunately clobbered by config1x.h etc
	// #define LZO_DETERMINISTIC (1)

	// NOT a macro because liblzo2 assumes that if UA_GET32 is a macro,
	// then it is faster than byte-array accesses, which it is not -
	// or, if it is, hotspot will deal with it.
	private static int UA_GET32(byte[] in, int in_ptr) {
		return (((in[in_ptr]) & 0xff) << 24) | (((in[in_ptr + 1]) & 0xff) << 16) | (((in[in_ptr + 2]) & 0xff) << 8)
				| ((in[in_ptr + 3]) & 0xff);
	}

	/*
	 * config1x.h -- configuration for the LZO1X algorithm
	 * 
	 * This file is part of the LZO real-time data compression library.
	 * 
	 * Copyright (C) 2011 Markus Franz Xaver Johannes Oberhumer Copyright (C) 2010
	 * Markus Franz Xaver Johannes Oberhumer Copyright (C) 2009 Markus Franz Xaver
	 * Johannes Oberhumer Copyright (C) 2008 Markus Franz Xaver Johannes Oberhumer
	 * Copyright (C) 2007 Markus Franz Xaver Johannes Oberhumer Copyright (C) 2006
	 * Markus Franz Xaver Johannes Oberhumer Copyright (C) 2005 Markus Franz Xaver
	 * Johannes Oberhumer Copyright (C) 2004 Markus Franz Xaver Johannes Oberhumer
	 * Copyright (C) 2003 Markus Franz Xaver Johannes Oberhumer Copyright (C) 2002
	 * Markus Franz Xaver Johannes Oberhumer Copyright (C) 2001 Markus Franz Xaver
	 * Johannes Oberhumer Copyright (C) 2000 Markus Franz Xaver Johannes Oberhumer
	 * Copyright (C) 1999 Markus Franz Xaver Johannes Oberhumer Copyright (C) 1998
	 * Markus Franz Xaver Johannes Oberhumer Copyright (C) 1997 Markus Franz Xaver
	 * Johannes Oberhumer Copyright (C) 1996 Markus Franz Xaver Johannes Oberhumer
	 * All Rights Reserved.
	 * 
	 * The LZO library is free software; you can redistribute it and/or modify it
	 * under the terms of the GNU General Public License as published by the Free
	 * Software Foundation; either version 2 of the License, or (at your option) any
	 * later version.
	 * 
	 * The LZO library is distributed in the hope that it will be useful, but
	 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
	 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
	 * details.
	 * 
	 * You should have received a copy of the GNU General Public License along with
	 * the LZO library; see the file COPYING. If not, write to the Free Software
	 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
	 * USA.
	 * 
	 * Markus F.X.J. Oberhumer <markus@oberhumer.com>
	 * http://www.oberhumer.com/opensource/lzo/
	 */

	/*
	 * WARNING: this file should *not* be used by applications. It is part of the
	 * implementation of the library and is subject to change.
	 */

	/*
	 * lzo_conf.h -- main internal configuration file for the the LZO library
	 * 
	 * This file is part of the LZO real-time data compression library.
	 * 
	 * Copyright (C) 2011 Markus Franz Xaver Johannes Oberhumer Copyright (C) 2010
	 * Markus Franz Xaver Johannes Oberhumer Copyright (C) 2009 Markus Franz Xaver
	 * Johannes Oberhumer Copyright (C) 2008 Markus Franz Xaver Johannes Oberhumer
	 * Copyright (C) 2007 Markus Franz Xaver Johannes Oberhumer Copyright (C) 2006
	 * Markus Franz Xaver Johannes Oberhumer Copyright (C) 2005 Markus Franz Xaver
	 * Johannes Oberhumer Copyright (C) 2004 Markus Franz Xaver Johannes Oberhumer
	 * Copyright (C) 2003 Markus Franz Xaver Johannes Oberhumer Copyright (C) 2002
	 * Markus Franz Xaver Johannes Oberhumer Copyright (C) 2001 Markus Franz Xaver
	 * Johannes Oberhumer Copyright (C) 2000 Markus Franz Xaver Johannes Oberhumer
	 * Copyright (C) 1999 Markus Franz Xaver Johannes Oberhumer Copyright (C) 1998
	 * Markus Franz Xaver Johannes Oberhumer Copyright (C) 1997 Markus Franz Xaver
	 * Johannes Oberhumer Copyright (C) 1996 Markus Franz Xaver Johannes Oberhumer
	 * All Rights Reserved.
	 * 
	 * The LZO library is free software; you can redistribute it and/or modify it
	 * under the terms of the GNU General Public License as published by the Free
	 * Software Foundation; either version 2 of the License, or (at your option) any
	 * later version.
	 * 
	 * The LZO library is distributed in the hope that it will be useful, but
	 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
	 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
	 * details.
	 * 
	 * You should have received a copy of the GNU General Public License along with
	 * the LZO library; see the file COPYING. If not, write to the Free Software
	 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
	 * USA.
	 * 
	 * Markus F.X.J. Oberhumer <markus@oberhumer.com>
	 * http://www.oberhumer.com/opensource/lzo/
	 */

	/*
	 * WARNING: this file should *not* be used by applications. It is part of the
	 * implementation of the library and is subject to change.
	 */

	/***********************************************************************
	 * // pragmas
	 ************************************************************************/

	/***********************************************************************
	 * //
	 ************************************************************************/

	/*
	 * ACC --- Automatic Compiler Configuration
	 * 
	 * This file is part of the LZO real-time data compression library.
	 * 
	 * Copyright (C) 2011 Markus Franz Xaver Johannes Oberhumer Copyright (C) 2010
	 * Markus Franz Xaver Johannes Oberhumer Copyright (C) 2009 Markus Franz Xaver
	 * Johannes Oberhumer Copyright (C) 2008 Markus Franz Xaver Johannes Oberhumer
	 * Copyright (C) 2007 Markus Franz Xaver Johannes Oberhumer Copyright (C) 2006
	 * Markus Franz Xaver Johannes Oberhumer Copyright (C) 2005 Markus Franz Xaver
	 * Johannes Oberhumer Copyright (C) 2004 Markus Franz Xaver Johannes Oberhumer
	 * Copyright (C) 2003 Markus Franz Xaver Johannes Oberhumer Copyright (C) 2002
	 * Markus Franz Xaver Johannes Oberhumer Copyright (C) 2001 Markus Franz Xaver
	 * Johannes Oberhumer Copyright (C) 2000 Markus Franz Xaver Johannes Oberhumer
	 * Copyright (C) 1999 Markus Franz Xaver Johannes Oberhumer Copyright (C) 1998
	 * Markus Franz Xaver Johannes Oberhumer Copyright (C) 1997 Markus Franz Xaver
	 * Johannes Oberhumer Copyright (C) 1996 Markus Franz Xaver Johannes Oberhumer
	 * All Rights Reserved.
	 * 
	 * The LZO library is free software; you can redistribute it and/or modify it
	 * under the terms of the GNU General Public License as published by the Free
	 * Software Foundation; either version 2 of the License, or (at your option) any
	 * later version.
	 * 
	 * The LZO library is distributed in the hope that it will be useful, but
	 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
	 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
	 * details.
	 * 
	 * You should have received a copy of the GNU General Public License along with
	 * the LZO library; see the file COPYING. If not, write to the Free Software
	 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
	 * USA.
	 * 
	 * Markus F.X.J. Oberhumer <markus@oberhumer.com>
	 * http://www.oberhumer.com/opensource/lzo/
	 */

	/* vim:set ts=4 et: */

	// Java # define assert(e) ((void)0)

	/***********************************************************************
	 * //
	 ************************************************************************/

	/* this always fits into 16 bits */
	// Java #define LZO_SIZE(bits) (1u << (bits))

	// Java #define LZO_LSIZE(bits) (1ul << (bits))

	/***********************************************************************
	 * // compiler and architecture specific stuff
	 ************************************************************************/

	/*
	 * Some defines that indicate if memory can be accessed at unaligned memory
	 * addresses. You should also test that this is actually faster even if it is
	 * allowed by your system.
	 */

	/*
	 * Fast memcpy that copies multiples of 8 byte chunks. len is the number of
	 * bytes. note: all parameters must be lvalues, len >= 8 dest and src advance,
	 * len is undefined afterwards
	 */

	/***********************************************************************
	 * // some globals
	 ************************************************************************/

	// Java LZO_EXTERN(const lzo_bytep) lzo_copyright(void);

	/***********************************************************************
	 * //
	 ************************************************************************/

	// Java #include "lzo_ptr.h"

	/*
	 * Generate compressed data in a deterministic way. This is fully portable, and
	 * compression can be faster as well. A reason NOT to be deterministic is when
	 * the block size is very small (e.g. 8kB) or the dictionary is big, because
	 * then the initialization of the dictionary becomes a relevant magnitude for
	 * compression speed.
	 */

	// Java # define lzo_dict_t lzo_uint
	// Java # define lzo_dict_p lzo_dict_t __LZO_MMODEL *

	/*
	 * vi:ts=4:et
	 */

	/*
	 * Memory required for the wrkmem parameter. When the required size is 0, you
	 * can also pass a NULL pointer.
	 */

	/***********************************************************************
	 * //
	 ************************************************************************/

	/***********************************************************************
	 * //
	 ************************************************************************/

	/*
	 * lzo_dict.h -- dictionary definitions for the the LZO library
	 * 
	 * This file is part of the LZO real-time data compression library.
	 * 
	 * Copyright (C) 2011 Markus Franz Xaver Johannes Oberhumer Copyright (C) 2010
	 * Markus Franz Xaver Johannes Oberhumer Copyright (C) 2009 Markus Franz Xaver
	 * Johannes Oberhumer Copyright (C) 2008 Markus Franz Xaver Johannes Oberhumer
	 * Copyright (C) 2007 Markus Franz Xaver Johannes Oberhumer Copyright (C) 2006
	 * Markus Franz Xaver Johannes Oberhumer Copyright (C) 2005 Markus Franz Xaver
	 * Johannes Oberhumer Copyright (C) 2004 Markus Franz Xaver Johannes Oberhumer
	 * Copyright (C) 2003 Markus Franz Xaver Johannes Oberhumer Copyright (C) 2002
	 * Markus Franz Xaver Johannes Oberhumer Copyright (C) 2001 Markus Franz Xaver
	 * Johannes Oberhumer Copyright (C) 2000 Markus Franz Xaver Johannes Oberhumer
	 * Copyright (C) 1999 Markus Franz Xaver Johannes Oberhumer Copyright (C) 1998
	 * Markus Franz Xaver Johannes Oberhumer Copyright (C) 1997 Markus Franz Xaver
	 * Johannes Oberhumer Copyright (C) 1996 Markus Franz Xaver Johannes Oberhumer
	 * All Rights Reserved.
	 * 
	 * The LZO library is free software; you can redistribute it and/or modify it
	 * under the terms of the GNU General Public License as published by the Free
	 * Software Foundation; either version 2 of the License, or (at your option) any
	 * later version.
	 * 
	 * The LZO library is distributed in the hope that it will be useful, but
	 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
	 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
	 * details.
	 * 
	 * You should have received a copy of the GNU General Public License along with
	 * the LZO library; see the file COPYING. If not, write to the Free Software
	 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
	 * USA.
	 * 
	 * Markus F.X.J. Oberhumer <markus@oberhumer.com>
	 * http://www.oberhumer.com/opensource/lzo/
	 */

	/*
	 * WARNING: this file should *not* be used by applications. It is part of the
	 * implementation of the library and is subject to change.
	 */

	/***********************************************************************
	 * // dictionary size
	 ************************************************************************/

	/* dictionary needed for compression */

	/* dictionary depth */

	/* dictionary length */

	/***********************************************************************
	 * // dictionary access
	 ************************************************************************/

	/* incremental LZO hash version B */

	/***********************************************************************
	 * // dictionary updating
	 ************************************************************************/

	/***********************************************************************
	 * // test for a match
	 ************************************************************************/

	/*
	 * vi:ts=4:et
	 */

	/*
	 * vi:ts=4:et
	 */

	/*
	 * lzo1x_c.ch -- implementation of the LZO1[XY]-1 compression algorithm
	 * 
	 * This file is part of the LZO real-time data compression library.
	 * 
	 * Copyright (C) 2011 Markus Franz Xaver Johannes Oberhumer Copyright (C) 2010
	 * Markus Franz Xaver Johannes Oberhumer Copyright (C) 2009 Markus Franz Xaver
	 * Johannes Oberhumer Copyright (C) 2008 Markus Franz Xaver Johannes Oberhumer
	 * Copyright (C) 2007 Markus Franz Xaver Johannes Oberhumer Copyright (C) 2006
	 * Markus Franz Xaver Johannes Oberhumer Copyright (C) 2005 Markus Franz Xaver
	 * Johannes Oberhumer Copyright (C) 2004 Markus Franz Xaver Johannes Oberhumer
	 * Copyright (C) 2003 Markus Franz Xaver Johannes Oberhumer Copyright (C) 2002
	 * Markus Franz Xaver Johannes Oberhumer Copyright (C) 2001 Markus Franz Xaver
	 * Johannes Oberhumer Copyright (C) 2000 Markus Franz Xaver Johannes Oberhumer
	 * Copyright (C) 1999 Markus Franz Xaver Johannes Oberhumer Copyright (C) 1998
	 * Markus Franz Xaver Johannes Oberhumer Copyright (C) 1997 Markus Franz Xaver
	 * Johannes Oberhumer Copyright (C) 1996 Markus Franz Xaver Johannes Oberhumer
	 * All Rights Reserved.
	 * 
	 * The LZO library is free software; you can redistribute it and/or modify it
	 * under the terms of the GNU General Public License as published by the Free
	 * Software Foundation; either version 2 of the License, or (at your option) any
	 * later version.
	 * 
	 * The LZO library is distributed in the hope that it will be useful, but
	 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
	 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
	 * details.
	 * 
	 * You should have received a copy of the GNU General Public License along with
	 * the LZO library; see the file COPYING. If not, write to the Free Software
	 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
	 * USA.
	 * 
	 * Markus F.X.J. Oberhumer <markus@oberhumer.com>
	 * http://www.oberhumer.com/opensource/lzo/
	 */

	/* choose a unique name to better help PGO optimizations */

	/*
	 * lzo_func.ch -- functions
	 * 
	 * This file is part of the LZO real-time data compression library.
	 * 
	 * Copyright (C) 2011 Markus Franz Xaver Johannes Oberhumer Copyright (C) 2010
	 * Markus Franz Xaver Johannes Oberhumer Copyright (C) 2009 Markus Franz Xaver
	 * Johannes Oberhumer Copyright (C) 2008 Markus Franz Xaver Johannes Oberhumer
	 * Copyright (C) 2007 Markus Franz Xaver Johannes Oberhumer Copyright (C) 2006
	 * Markus Franz Xaver Johannes Oberhumer Copyright (C) 2005 Markus Franz Xaver
	 * Johannes Oberhumer Copyright (C) 2004 Markus Franz Xaver Johannes Oberhumer
	 * Copyright (C) 2003 Markus Franz Xaver Johannes Oberhumer Copyright (C) 2002
	 * Markus Franz Xaver Johannes Oberhumer Copyright (C) 2001 Markus Franz Xaver
	 * Johannes Oberhumer Copyright (C) 2000 Markus Franz Xaver Johannes Oberhumer
	 * Copyright (C) 1999 Markus Franz Xaver Johannes Oberhumer Copyright (C) 1998
	 * Markus Franz Xaver Johannes Oberhumer Copyright (C) 1997 Markus Franz Xaver
	 * Johannes Oberhumer Copyright (C) 1996 Markus Franz Xaver Johannes Oberhumer
	 * All Rights Reserved.
	 * 
	 * The LZO library is free software; you can redistribute it and/or modify it
	 * under the terms of the GNU General Public License as published by the Free
	 * Software Foundation; either version 2 of the License, or (at your option) any
	 * later version.
	 * 
	 * The LZO library is distributed in the hope that it will be useful, but
	 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
	 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
	 * details.
	 * 
	 * You should have received a copy of the GNU General Public License along with
	 * the LZO library; see the file COPYING. If not, write to the Free Software
	 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
	 * USA.
	 * 
	 * Markus F.X.J. Oberhumer <markus@oberhumer.com>
	 * http://www.oberhumer.com/opensource/lzo/
	 */

	/*
	 * WARNING: this file should *not* be used by applications. It is part of the
	 * implementation of the library and is subject to change.
	 */

	/***********************************************************************
	 * // bitops
	 ************************************************************************/

	/*
	 * vi:ts=4:et
	 */

	// Java addition: {

	private static final int init = 0;
	private static final int next = 1;
	private static final int try_match = 2;
	private static final int literal = 3;
	private static final int m_len_done = 4;
	// End Java addition: }

	/***********************************************************************
	 * // compress a block of data.
	 ************************************************************************/

	static int compress_core(byte[] in, int in_base, int in_len, byte[] out, int out_base, lzo_uintp out_len,
			// Java lzo_uint ti, lzo_voidp wrkmem)
			int ti, int[] dict) {
		;
		// java register const lzo_bytep ip;
		int in_ptr = in_base;
		// Java lzo_bytep op;
		int out_ptr = out_base;
		// Java const lzo_bytep const in_end = in + in_len;
		int in_end = in_base + in_len;
		// Java const lzo_bytep const ip_end = in + in_len - 20;
		int ip_end = in_base + in_len - 20;
		// Java const lzo_bytep ii;
		int ii;
		// Java lzo_dict_p const dict = (lzo_dict_p) wrkmem;

		// Java op = out;
		// Java ip = in;
		// Java ii = ip - ti;
		ii = in_ptr - ti;

		int state = init;

		// Java ip += ti < 4 ? 4 - ti : 0;
		// Java-note Make sure we have at least 4 bytes after ii.
		in_ptr += ti < 4 ? 4 - ti : 0;
		int m_pos = Integer.MIN_VALUE;
		int m_len = Integer.MIN_VALUE;

		int m_off = Integer.MIN_VALUE;

		GOTO_LOOP: for (;;) {
			switch (state) {
			case init: // Java-GOTO
				// Java const lzo_bytep m_pos;
				// Java-moved lzo_uint m_pos;

				// Java-moved lzo_uint m_off;
				// Java-moved lzo_uint m_len;
				// Java {
				int dv;
				int dindex;
			case literal:
				;
				// Java ip += 1 + ((ip - ii) >> 5);
				in_ptr += 1 + ((in_ptr - ii) >> 5);
			case next:
				;
				// Java if __lzo_unlikely(ip >= ip_end)
				if (in_ptr >= ip_end)
					break GOTO_LOOP;
				// Java dv = UA_GET32(ip);
				dv = UA_GET32(in, in_ptr);
				;
				dindex = ((int) (((((((long) ((0x1824429d) * (dv)))) >> (32 - 14)))
						& (((1 << (14)) - 1) >> (0))) << (0)));
				;
				// Java GINDEX(m_off,m_pos,in+dict,dindex,in);
				m_pos = in_base + dict[dindex];
				// Java UPDATE_I(dict,0,dindex,ip,in);
				dict[dindex] = ((int) ((in_ptr) - (in_base)));
				// Java if __lzo_unlikely(dv != UA_GET32(m_pos))
				;
				if (dv != UA_GET32(in, m_pos))
					do {
						state = literal;
						continue GOTO_LOOP;
					} while (false);
			// Java }

			/* a match */

			{
				// Java register lzo_uint t = pd(ip,ii);
				int t = ((in_ptr) - (ii));
				;
				if (t != 0) {
					if (t <= 3) {
						// Java op[-2] |= LZO_BYTE(t);
						out[out_ptr - 2] |= ((byte) (t));

						// Java { do *op++ = *ii++; while (--t > 0); }
						{
							do
								out[out_ptr++] = in[ii++];
							while (--t > 0);
						}

					}

					else {
						if (t <= 18)
							// Java *op++ = LZO_BYTE(t - 3);
							out[out_ptr++] = ((byte) (t - 3));
						else {
							int tt = t - 18;
							// Java *op++ = 0;
							out[out_ptr++] = 0;
							while (tt > 255) {
								tt -= 255;

								// Java *op++ = 0;
								out[out_ptr++] = 0;

							}
							// TODO assert (tt > 0) : "Assertion failed: " + "tt > 0";

							// Java *op++ = LZO_BYTE(tt);
							out[out_ptr++] = ((byte) (tt));
						}

						// Java { do *op++ = *ii++; while (--t > 0); }
						{
							do
								out[out_ptr++] = in[ii++];
							while (--t > 0);
						}
					}
				}
			}
				m_len = 4; {

				// Java if __lzo_unlikely(ip[m_len] == m_pos[m_len]) {
				;
				if (in[in_ptr + m_len] == in[m_pos + m_len]) {
					do {
						m_len += 1;
						// Java if __lzo_unlikely(ip + m_len >= ip_end)
						if (in_ptr + m_len >= ip_end) {
							;
							do {
								state = m_len_done;
								continue GOTO_LOOP;
							} while (false);
						}
						// Java } while (ip[m_len] == m_pos[m_len]);
					} while (in[in_ptr + m_len] == in[m_pos + m_len]);
				}

			}
			case m_len_done:
				;
				// Java m_off = pd(ip,m_pos);
				m_off = ((in_ptr) - (m_pos));
				;
				// Java ip += m_len;
				in_ptr += m_len;
				// Java ii = ip;
				ii = in_ptr;
				if (m_len <= 8 && m_off <= 0x0800) {
					;
					m_off -= 1;

					// Java *op++ = LZO_BYTE(((m_len - 1) << 5) | ((m_off & 7) << 2));
					out[out_ptr++] = ((byte) (((m_len - 1) << 5) | ((m_off & 7) << 2)));
					// Java *op++ = LZO_BYTE(m_off >> 3);
					out[out_ptr++] = ((byte) (m_off >> 3));

				} else if (m_off <= 0x4000) {
					;
					m_off -= 1;
					if (m_len <= 33)
						// Java *op++ = LZO_BYTE(M3_MARKER | (m_len - 2));
						out[out_ptr++] = ((byte) (32 | (m_len - 2)));
					else {
						m_len -= 33;
						// Java *op++ = M3_MARKER | 0;
						out[out_ptr++] = 32 | 0;
						while (m_len > 255) {
							m_len -= 255;

							// Java *op++ = 0;
							out[out_ptr++] = 0;

						}
						// Java *op++ = LZO_BYTE(m_len);
						out[out_ptr++] = ((byte) (m_len));
					}
					// Java *op++ = LZO_BYTE(m_off << 2);
					out[out_ptr++] = ((byte) (m_off << 2));
					// Java *op++ = LZO_BYTE(m_off >> 6);
					out[out_ptr++] = ((byte) (m_off >> 6));
				} else {
					;
					m_off -= 0x4000;
					if (m_len <= 9)
						// Java *op++ = LZO_BYTE(M4_MARKER | ((m_off >> 11) & 8) | (m_len - 2));
						out[out_ptr++] = ((byte) (16 | ((m_off >> 11) & 8) | (m_len - 2)));
					else {
						m_len -= 9;
						// Java *op++ = LZO_BYTE(M4_MARKER | ((m_off >> 11) & 8));
						out[out_ptr++] = ((byte) (16 | ((m_off >> 11) & 8)));
						while (m_len > 255) {
							m_len -= 255;

							// Java *op++ = 0;
							out[out_ptr++] = 0;

						}
						// Java *op++ = LZO_BYTE(m_len);
						out[out_ptr++] = ((byte) (m_len));
					}
					// Java *op++ = LZO_BYTE(m_off << 2);
					out[out_ptr++] = ((byte) (m_off << 2));
					// Java *op++ = LZO_BYTE(m_off >> 6);
					out[out_ptr++] = ((byte) (m_off >> 6));
				}
				do {
					state = next;
					continue GOTO_LOOP;
				} while (false);
			default:
				try {
					throw new Exception("Unknown state " + state);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} // JAVA_GOTO
		}

		// Java *out_len = pd(op, out);
		out_len.value = out_ptr - out_base;
		return ((in_end) - (ii));
	}

	/***********************************************************************
	 * // public entry point
	 ************************************************************************/

	public static int compress(byte[] in, int in_base, int in_len, byte[] out, int out_base, lzo_uintp out_len,
			Object wrkmem) {
		// Java const lzo_bytep ip = in;
		int in_ptr = in_base;
		// lzo_bytep op = out;
		int out_ptr = out_base;
		int l = in_len;
		int t = 0;

		while (l > 20) {
			int ll = l;
			int ll_end;

			ll = ((ll) <= (49152) ? (ll) : (49152));

			// Java ll_end = (lzo_uintptr_t)ip + ll;
			ll_end = (int) in_ptr + ll;
			// Java if ((ll_end + ((t + ll) >> 5)) <= ll_end || (const lzo_bytep)(ll_end +
			// ((t + ll) >> 5)) <= ip + ll)
			if ((ll_end + ((t + ll) >> 5)) <= ll_end /* || (const lzo_bytep)(ll_end + ((t + ll) >> 5)) <= ip + ll */)
				break;
			int[] dict = (int[]) wrkmem; // Java only

			// Java lzo_memset(wrkmem, 0, ((lzo_uint)1 << D_BITS) * sizeof(lzo_dict_t));
			// Arrays.fill(dict, 0);
			for (int i = 0; i < dict.length; i++) {
				dict[i] = 0;
			}

			// Java t = do_compress(ip,ll,op,out_len,t,wrkmem);
			t = compress_core(in, in_ptr, ll, out, out_ptr, out_len, t, dict);
			// Java ip += ll;
			in_ptr += ll;
			// Java op += *out_len;
			out_ptr += out_len.value;
			l -= ll;
		}
		t += l;
		;

		if (t > 0) {
			// Java const lzo_bytep ii = in + in_len - t;
			int ii = in_base + in_len - t;

			// Java if (op == out && t <= 238)
			if (out_ptr == out_base && t <= 238)
				// Java *op++ = LZO_BYTE(17 + t);
				out[out_ptr++] = ((byte) (17 + t));
			else if (t <= 3)
				// Java op[-2] |= LZO_BYTE(t);
				out[out_ptr - 2] |= ((byte) (t));
			else if (t <= 18)
				// Java *op++ = LZO_BYTE(t - 3);
				out[out_ptr++] = ((byte) (t - 3));
			else {
				int tt = t - 18;

				// Java *op++ = 0;
				out[out_ptr++] = 0;
				while (tt > 255) {
					tt -= 255;

					// Java *op++ = 0;
					out[out_ptr++] = 0;

				}
				// TODO assert (tt > 0) : "Assertion failed: " + "tt > 0";

				// Java *op++ = LZO_BYTE(tt);
				out[out_ptr++] = ((byte) (tt));
			}
			// Java do *op++ = *ii++; while (--t > 0);
			do
				out[out_ptr++] = in[ii++];
			while (--t > 0);
		}

		// Java *op++ = M4_MARKER | 1;
		out[out_ptr++] = 16 | 1;
		// Java *op++ = 0;
		out[out_ptr++] = 0;
		// Java *op++ = 0;
		out[out_ptr++] = 0;

		// Java *out_len = pd(op, out);
		out_len.value = out_ptr - out_base;
		return LZO_E_OK;
	}

	/*
	 * vi:ts=4:et
	 */

	private final int[] dictionary = new int[1 << 14];

	public int compress(byte[] in, int in_base, int in_len, byte[] out, int out_base, lzo_uintp out_len) {
		return compress(in, in_base, in_len, out, out_base, out_len, dictionary);
	}
}
