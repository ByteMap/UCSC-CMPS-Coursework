#!/usr/bin/python

from mininet.topo import Topo
from mininet.net import Mininet
from mininet.cli import CLI

class MyTopology(Topo):
    """
    A basic topology
    """
    def __init__(self):
        Topo.__init__(self)

        # Set Up Topology Here
        switch1 = self.addSwitch('s1')    ## Adds a Switch1
        switch2 = self.addSwitch('s2')    ## Adds a Switch2
        switch3 = self.addSwitch('s3')    ## Adds a Switch3

        host1 = self.addHost('h1')       ## Adds a Host1
        host2 = self.addHost('h2')       ## Adds a Host2
        host3 = self.addHost('h3')       ## Adds a Host3
        host4 = self.addHost('h4')       ## Adds a Host4
        host5 = self.addHost('h5')       ## Adds a Host5
        host6 = self.addHost('h6')       ## Adds a Host6

        self.addLink(host1, switch1)     ## Connects host1 to switch1
        self.addLink(host3, switch1)     ## Connects host3 to switch1
        self.addLink(host2, switch2)     ## Connects host2 to switch2
        self.addLink(host4, switch2)     ## Connects host4 to switch2
        self.addLink(host5, switch3)     ## Connects host5 to switch3
        self.addLink(host6, switch3)     ## Connects host6 to switch3

        self.addLink(switch1, switch3)   ## Connects switch1 to switch3
        self.addLink(switch2, switch3)   ## Connects switch2 to switch3

if __name__ == '__main__':
    """
    If this script is run as an executable (by chmod +x), this is
    what it will do
    """

    topo = MyTopology()            ## Creates the topology
    net = Mininet( topo=topo )        ## Loads the topology
    net.start()                      ## Starts Mininet

    # Commands here will run on the simulated topology
    CLI(net)

    net.stop()                       ## Stops Mininet
