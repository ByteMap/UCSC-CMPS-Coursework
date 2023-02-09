# Final Skeleton
#
# Hints/Reminders from Lab 4:
# 
# To send an OpenFlow Message telling a switch to send packets out a
# port, do the following, replacing <PORT> with the port number the 
# switch should send the packets out:
#
#    msg = of.ofp_flow_mod()
#    msg.match = of.ofp_match.from_packet(packet)
#    msg.idle_timeout = 30
#    msg.hard_timeout = 30
#
#    msg.actions.append(of.ofp_action_output(port = <PORT>))
#    msg.data = packet_in
#    self.connection.send(msg)
#
# To drop packets, simply omit the action.
#

from pox.core import core
import pox.openflow.libopenflow_01 as of

log = core.getLogger()

class Final (object):
  """
  A Firewall object is created for each switch that connects.
  A Connection object for that switch is passed to the __init__ function.
  """
  def __init__ (self, connection):
    # Keep track of the connection to the switch so that we can
    # send it messages!
    self.connection = connection

    # This binds our PacketIn event listener
    connection.addListeners(self)

  def accept_packet (self, packet_in, packet, out_port):
    print out_port
    rule = of.ofp_flow_mod()
    rule.match = of.ofp_match.from_packet(packet)
    rule.actions.append(of.ofp_action_output(port = out_port))
    self.connection.send(rule)

    message = of.ofp_packet_out(data = packet_in)
    message.actions.append(of.ofp_action_output(port = out_port))
    self.connection.send(message)

  def drop_packet (self, packet, packet_in):
    rule = of.ofp_flow_mod()
    rule.match = of.ofp_match.from_packet(packet)
    rule.actions.append(of.ofp_action_output(port = of.OFPP_NONE))
    self.connection.send(rule)

    message = of.ofp_packet_out(data = packet_in)
    message.actions.append(of.ofp_action_output(port = of.OFPP_NONE))
    self.connection.send(message)

  def do_final (self, packet, packet_in, port_on_switch, switch_id):
    # This is where you'll put your code. The following modifications have 
    # been made from Lab 4:
    #   - port_on_switch represents the port that the packet was received on.
    #   - switch_id represents the id of the switch that received the packet
    #      (for example, s1 would have switch_id == 1, s2 would have switch_id == 2, etc...)
    TCP  = packet.find('tcp')
    ARP  = packet.find('arp')
    ICMP = packet.find('icmp')
    if ARP:
        rule = of.ofp_flow_mod()
        rule.match = of.ofp_match.from_packet(packet)
        rule.actions.append(of.ofp_action_output(port = of.OFPP_FLOOD))
        self.connection.send(rule)

        message = of.ofp_packet_out(data = packet_in)
        message.actions.append(of.ofp_action_output(port = of.OFPP_FLOOD))
        self.connection.send(message)

    if TCP or ICMP:
      if switch_id == 1:
        if port_on_switch == 1:
          self.accept_packet(packet_in, packet, 11)
        elif port_on_switch == 11:
          self.accept_packet(packet_in, packet, 1)

      elif switch_id == 2:
        if port_on_switch == 2:
          self.accept_packet(packet_in, packet, 21)
        elif port_on_switch == 21:
          self.accept_packet(packet_in, packet, 2)

      elif switch_id == 3:
        if port_on_switch == 3:
          self.accept_packet(packet_in, packet, 31)
        elif port_on_switch == 31:
          self.accept_packet(packet_in, packet, 3)

      elif switch_id == 4:
        ip = packet.find('ipv4')
        print ip.srcip, ip.dstip
        if ip.srcip == "128.114.50.10" and ICMP:
          self.drop_packet(packet, packet_in)
        elif ip.dstip == "10.0.1.101":
          self.accept_packet(packet_in, packet, 41)
        elif ip.dstip == "10.0.2.102":
          self.accept_packet(packet_in, packet, 42)
        elif ip.dstip == "10.0.3.103":
          self.accept_packet(packet_in, packet, 43)
        elif ip.dstip == "10.0.4.104":
          if ip.srcip == "128.114.50.10":
            self.drop_packet(packet, packet_in)
          else:
            self.accept_packet(packet_in, packet, 44)
        elif ip.dstip == "128.114.50.10":
          self.accept_packet(packet_in, packet, 4)

      elif switch_id == 5:
        if port_on_switch == 5:
          self.accept_packet(packet_in, packet, 51)
        elif port_on_switch == 51:
          self.accept_packet(packet_in, packet, 5)

  def _handle_PacketIn (self, event):
    """
    Handles packet in messages from the switch.
    """
    packet = event.parsed # This is the parsed packet data.
    if not packet.parsed:
      log.warning("Ignoring incomplete packet")
      return

    packet_in = event.ofp # The actual ofp_packet_in message.
    self.do_final(packet, packet_in, event.port, event.dpid)

def launch ():
  """
  Starts the component
  """
  def start_switch (event):
    log.debug("Controlling %s" % (event.connection,))
    Final(event.connection)
  core.openflow.addListenerByName("ConnectionUp", start_switch)
