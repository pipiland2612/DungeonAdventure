package system.ui

import game.GamePanel
import PlayerUI.tileSize
import utils.Tools

import java.awt.Graphics2D

object TradeUI:
  var g2: Graphics2D = _
  var gp: GamePanel = _
  def setGraphics(g: Graphics2D, gamePanel: GamePanel): Unit =
    g2 = g
    gp = gamePanel
    tileSize = gp.tileSize

  def drawTradeState(): Unit =
    gp.gui.subState match
      case 0 => selectTrade()
      case 1 => buyTrade()
      case 2 => sellTrade()
      case _ =>
    gp.keyH.enterPressed = false

  def selectTrade(): Unit =
    gp.gui.npc.dialogueSet = 0
    gp.gui.drawDialogueScreen()
    var x = tileSize * 15
    var y = tileSize * 4 + 10
    val width = tileSize * 3
    val height = (tileSize * 3.5).toInt
    Tools.drawSubWindow(g2, x, y, width, height)

    x += tileSize
    y += tileSize
    g2.drawString("Buy", x, y)
    if gp.gui.commandNum == 0 then
      g2.drawString(">", x - 20, y)
      if gp.keyH.enterPressed then
        gp.gui.subState = 1

    y += tileSize
    g2.drawString("Sell", x, y)
    if gp.gui.commandNum == 1 then
      g2.drawString(">", x - 20, y)
      if gp.keyH.enterPressed then
        gp.gui.subState = 2

    y += tileSize
    g2.drawString("Leave", x, y)
    if gp.gui.commandNum == 2 then
      g2.drawString(">", x - 20, y)
      if gp.keyH.enterPressed then
        gp.gui.commandNum = 0
        gp.gui.npc.startDialogue(gp.gui.npc, 3)

  def buyTrade(): Unit =
    PlayerUI.drawInventory(gp.player, false)
    PlayerUI.drawInventory(gp.gui.npc, true)

    var x = tileSize * 2
    var y = tileSize * 9
    var width = tileSize * 6
    var height = tileSize * 2
    Tools.drawSubWindow(g2, x, y, width, height)
    g2.drawString("[ESC] Back", x + 20, y + 30)

    var itemIndex = PlayerUI.getItemIndexBySlot(PlayerUI.npcSlotCol, PlayerUI.npcSlotRow)
    if itemIndex < gp.gui.npc.inventory.size then
      var price = gp.gui.npc.inventory(itemIndex).price
      g2.drawString(s"Cost: ${price}", (x + tileSize * 4) + 3, y - 20)
      if gp.keyH.enterPressed then
        if price > gp.player.coin then
          gp.gui.subState = 0
          gp.gui.npc.startDialogue(gp.gui.npc, 4)
        else
          if gp.player.obtainItem(gp.gui.npc.inventory(itemIndex)) then
            gp.player.coin -= price
          else
            gp.gui.subState = 0
            gp.gui.npc.startDialogue(gp.gui.npc, 5)

    x = gp.screenWidth - tileSize * 7
    y = tileSize * 9
    width = tileSize * 6
    height = tileSize * 2
    Tools.drawSubWindow(g2, x, y, width, height)
    g2.drawString(s"Your coin: ${gp.player.coin}", x + 24, y + 60)


  def sellTrade(): Unit =
    PlayerUI.drawInventory(gp.player, true)
    var x = tileSize * 2
    var y = tileSize * 9
    var width = tileSize * 6
    var height = tileSize * 2
    Tools.drawSubWindow(g2, x, y, width, height)
    g2.drawString("[ESC] Back", x + 20, y + 30)


    x = gp.screenWidth - tileSize * 7
    y = tileSize * 9
    width = tileSize * 6
    height = tileSize * 2
    Tools.drawSubWindow(g2, x, y, width, height)
    g2.drawString(s"Your coin: ${gp.player.coin}", x + 24, y + 60)

    val itemIndex = PlayerUI.getItemIndexBySlot(PlayerUI.playerSlotCol, PlayerUI.playerSlotRow)
    if itemIndex < gp.player.inventory.size then
      var price = gp.player.inventory(itemIndex).price / 2
      g2.drawString(s"Price: ${price}", (x + tileSize * 4) + 10, y - 18)
      // SELLING
      if gp.keyH.enterPressed then
        if gp.player.inventory(itemIndex) == gp.player.currentWeapon || gp.player.inventory(itemIndex) == gp.player.currentShield then
          gp.gui.commandNum = 0
          gp.gui.npc.startDialogue(gp.gui.npc, 6)
        else
          if gp.player.inventory(itemIndex).amount > 1 then
            gp.player.inventory(itemIndex).amount -= 1
          else
            gp.player.inventory.remove(itemIndex)
          gp.player.coin += price
