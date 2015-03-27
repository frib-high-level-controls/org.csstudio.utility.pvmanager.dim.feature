package org.epics.pvmanager.dim;

import org.epics.pvmanager.ChannelHandler;
import org.epics.pvmanager.DataSource;

import dim.DimClient;

public class DimDataSource extends DataSource {

	final private String dnsNodes;
	
	public DimDataSource() {
		super(true);
		this.dnsNodes="localhost";
		DimClient.setDnsNode(dnsNodes);
	}
	
	public DimDataSource(String dnsNodes) {
		super(true);
		this.dnsNodes=dnsNodes;
		DimClient.setDnsNode(dnsNodes);

	}
	
	public DimDataSource(DimDataSource dim) {
		super(true);
		this.dnsNodes=dim.dnsNodes;
		DimClient.setDnsNode(dim.dnsNodes);
	}

	@Override
	protected ChannelHandler createChannel(String channelName) {

		return new DimChannelHandler(channelName);
	}

}
