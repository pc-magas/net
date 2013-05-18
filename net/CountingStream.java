/**
*	HTTP Downloader lib
*	Copyright (C) 2012-2013  Dimitrios Desyllas (pc_magas)
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*Contact with me by main at thes address: pc_magas@yahoo.gr
*/
package net;

import java.awt.event.ActionListener;
import org.apache.commons.io.output.CountingOutputStream;
import java.io.*;
import java.awt.event.ActionEvent;


public class CountingStream extends CountingOutputStream 
{

	/**
	*The action Listener that "snifs" the chgange of file duting the download 
	*/
    private ActionListener l = null;

	/**
	*Constructor Method
	*@param out: the Stream that we listen 
	*/
    public CountingStream(OutputStream out) 
    {
        super(out);
    }

	/**
	*Setting a Listener to this Stream
	*@param l: the Listener that we set
	*/
    public void setListener(ActionListener l) 
    {
        this.l = l;
    }

    @Override
    protected void afterWrite(int n) throws IOException 
    {
        super.afterWrite(n);
        
        if (l != null) 
        {
            l.actionPerformed(new ActionEvent(this, 0, null));
        }
    }

}
