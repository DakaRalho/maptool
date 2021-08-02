package net.rptools.maptool.model.player;

import java.lang.reflect.InvocationTargetException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import javax.crypto.NoSuchPaddingException;
import net.rptools.lib.MD5Key;
import net.rptools.maptool.util.cipher.CipherUtil;

import java.util.Optional;

/**
 * This class provides the implementation for the default player database, where any one can connect as long as they
 * know the role password. This follows the standard behaviour for 1.9 and earlier.
 */
public class DefaultPlayerDatabase implements PlayerDatabase {

  private final CipherUtil playerPassword;
  private final CipherUtil gmPassword;

  DefaultPlayerDatabase(String playerPassword, String gmPassword)
      throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException {
    byte[] salt = CipherUtil.createSalt();
    this.playerPassword = CipherUtil.fromSharedKey(playerPassword, salt);
    this.gmPassword = CipherUtil.fromSharedKey(gmPassword, salt);
  }

  @Override
  public boolean playerExists(String playerName) {
    return true; // The player will always "exist" in the database as any player name is possible.
  }

  @Override
  public Player getPlayer(String playerName) {
    // If role is not specified always return player!
    return new Player(playerName, Player.Role.PLAYER, playerPassword.getKey());
  }

  @Override
  public Optional<CipherUtil.Key> getPlayerPassword(String playerName) {
    return Optional.empty(); // Only supports role based passwords.
  }

  @Override
  public byte[] getPlayerPasswordSalt(String playerName) {
    return playerPassword.getKey().salt(); // Player and GM password salt are the same
  }

  @Override
  public Player getPlayerWithRole(String playerName, Player.Role role) {
    return new Player(playerName, role, getRolePassword(role).get());
  }

  @Override
  public Optional<CipherUtil.Key> getRolePassword(Player.Role role) {
    switch (role) {
      case PLAYER:
        return Optional.of(playerPassword.getKey());
      case GM:
        return Optional.of(gmPassword.getKey());
      default:
        return Optional.empty();
    }
  }

  @Override
  public boolean supportsDisabling() {
    return false;
  }

  @Override
  public boolean supportsPlayTimes() {
    return false;
  }

  @Override
  public boolean supportsAsymmetricalKeys() {
    return false;
  }

  @Override
  public boolean supportsRolePasswords() {
    return true;
  }

  @Override
  public void disablePlayer(Player player, String reason) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isDisabled(Player player) {
    return false;
  }

  @Override
  public String getDisabledReason(Player player) {
    return "";
  }

  @Override
  public Set<PlayTime> getPlayTimes(Player player) {
    return ANY_TIME;
  }

  @Override
  public void setPlayTimes(Player player, Collection<PlayTime> times) {
    throw new UnsupportedOperationException();
  }

  @Override
  public AuthMethod getAuthMethod(Player player) {
    return AuthMethod.PASSWORD; // Will always be password based
  }

  @Override
  public CompletableFuture<CipherUtil> getPublicKey(Player player, MD5Key md5key) {
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public boolean isPlayerRegistered(String name)
      throws InterruptedException, InvocationTargetException {
    return false;
  }
}
