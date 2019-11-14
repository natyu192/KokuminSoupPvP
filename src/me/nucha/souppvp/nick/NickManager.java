package me.nucha.souppvp.nick;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.mojang.authlib.properties.Property;

import me.nucha.core.packet.PacketHandler;
import me.nucha.souppvp.SoupPvPPlugin;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PlayerConnection;

public class NickManager {

	private static NickListener nickListener;
	private static HashMap<String, Property> skins; // skins origin
	private static HashMap<Player, String> nicknamesUsed;
	private static HashMap<Player, String> displayNames;
	private static HashMap<Player, String> playerListNames;
	private static HashMap<Player, Property> properties; // storing nicked player's skins
	// private static HashMap<Player, EntityPlayer> entityPlayers;

	public static void init() {
		NickGenerator.init();
		nickListener = new NickListener();
		skins = new HashMap<>();
		skins.put("blondehairedgirl", new Property("textures",
				"eyJ0aW1lc3RhbXAiOjE1NTMzNTMwMjY4MTgsInByb2ZpbGVJZCI6ImE1OTQwMTgzOGZkOTQyNzJiOWVhMGQyOGM1NzgwZTY4IiwicHJvZmlsZU5hbWUiOiJrb2t1bWluIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jZWYwMjllNzlkZDQ4MTMxYTYwYzlhOWE2YTdlY2NkNzdjNWFjNDBiZDdiZDgxYjk4NDgxZjY0NDljNGE5MDVkIn19fQ==",
				"DV8XhjpRiX5NbF1i3w+Go5OelIlKp6X2OyWB0KNdwfSUHd5KTkhRtKwxVqu92rk6LtYTa85RLDBQMBOaeid1LlPhssLFeqPAgJHdjDiDUjFHzla7t3IzlKNyT0Ti9A/kSOFz+hydd4tM/kriPRXU7IVvKpioCQPnLVEO0iYOVAe90oVsMIg0EDUUju2vHZ12ULaSehjGdYYc5AXKj1ubEGxM5bcetJYIFfqI82stP7y4NRdo3K3f76TEQ+w0r09XRHHK0to0Eke5PCTC8GWsdmmx2E+kXDMHaP1yaYAnhCAVZSgls1VHo50esuWZea4Y36BqbsoOl7glK1D4KrKA1FZfzeqocmZKmcHVAIT4+PjAjZrRaGv56jHH8ZnZ5F6Oqpwk2eH79gT/aaPM7qZv8E1hgPPSyfa45Of2zF8eKGfR+R4ilyUrlNlYuza95coxu3nHU9Kkot99NceTgifv6bYm8ZIKH7wrm8kHpjWnfsq87/qpMToR6/kC5DlwUhqXGVB4jDCsALOWPUXobY451TNl5lrxHRgjzBSWCZ5ITjis++li3wRrDKpKiRtNO8YqTmMPxleDypTAamr0jBVOtmq6bG9gc7hSmZ9RVXMgPfw3xK03HvfXpCJzCiiSUCC1xsfCoEUNDozms1v6XoacWp5liwXhlE7v+dlR/SrT9es="));
		skins.put("depotagent", new Property("textures",
				"eyJ0aW1lc3RhbXAiOjE1NTMzNTMwODY5MjIsInByb2ZpbGVJZCI6ImE1OTQwMTgzOGZkOTQyNzJiOWVhMGQyOGM1NzgwZTY4IiwicHJvZmlsZU5hbWUiOiJrb2t1bWluIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83ZWRhZGIxMWQwMjc4ZDc1NDMwMDI2NmE1MTVlMjA1NTIyMzUwMGQ0NzFmNmNkMTkwNzlmYjIxMzE3YWU4NTE1In19fQ==",
				"ufPAaBhJrorOljj84R+eZMl590xUQJBm1zKHYlBfqTvgU2/XZVICSe8aRZj5Kjg8/nHOsj2ZoQlE1uk4NjBEZgMes7mgZZVWCwvPiZLliQ7P2dy581CQ3+YGxj6FTBdqxdhPH3HArdLtaJqv+Fc6PfKUHxwTGK06M6aohaCypyAj0fYLNwJYVyqilI3aXy/yq0hFEZ3pXWgm7wI6h5llnzNweJa4qiQIRMzXzeI8BGPRuyp31pS6NnWweBoChimCnkDpAc4tLmcRNokxeTgQYGb200tD5RoCUSmEwF4vs8G9KcZrlUPcN4ckldfC4t0jPWEbC3YTR47Q5rORz+4Vq5nYO0Dsv4OcCMsE4g7/qnJzhL2eKcvSIud5VD3fKHs8HIXh1gZ4zqXmZz6oPZbGAfipMvHklkd3EUanvGwI1VFiysqVIxslIaf1pVv8iENGehNMtvFA68X2SJ6usfVap+nIXdAjkZD8VVB8LkCE7t2zj/2iB1HrL1qzTBfGmL2xdZJJLSZNF/1VqXaf8wrr+wXHb8VI1iOw7faGJI1Nec4DDcVL6OIOatqVRx0tAac/vPKnC59a0r9mU1+i7dZUzYgLpvA6+Orf2dvWTN2uMrUuEBf50gyZusd2BuKeNyxtHsZS8Uagla0JP2tu+qC6pKVKkUxL8pLDRi7v8A9X4EY="));
		skins.put("elitra-bwm", new Property("textures",
				"eyJ0aW1lc3RhbXAiOjE1NTMzNTMxMzQ1NzAsInByb2ZpbGVJZCI6ImE1OTQwMTgzOGZkOTQyNzJiOWVhMGQyOGM1NzgwZTY4IiwicHJvZmlsZU5hbWUiOiJrb2t1bWluIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85OTJkYjljOWVmY2RkYTE0MDIxODBkZGQxNTBkM2VlNTU4YTMzZmM5MGE1YzIyY2Y4MTgzYmRiYjFiZjI1ZDA1In19fQ==",
				"ZyMfCMxQQBOiO4Zl8dqdLm/dTGM0KICke7fsnP43mAClZhRLB/eWVhxiPDfScM1dwdFpAtFFTraSTP/ruNgL95c4T6zkLiOX1TmdjtN9TEUq4lRnfbvGQZLPMslr5kqWaplxocFYzoP6Jv7qgwcUJ1DpLrM7JXabMh+w3Z21tzF/X8dgFWy1QJONos2qLkwwutDeSflsXbAQtVMPeYHJvnOK9OxomKnFUNYAZJRbVgQZr8eud2rH55tWgkFViXMz6MzzcMR+ryyYbbFV+IHzO2gGlie3adA9kqWwZLTLjNnuSBu88tBvLJiFZCCESuRpo16huB+m0Z/WWnTUmdo9xC05MpDq2oWTVMUY6LB9NB+V+kTWrsutCPpFQUgw9+g145Op7PPmWzHR044o9igyeQwbx8w5bllEbemXv6K2c7JCVcZrFBsFhh0Xjv1tpnmyG3KrvtcnfkNMMaibS6aHWjltUQRDkT18CE/D85gFHGAHfFnTzq5S0gSpCLy9gXMxfSCfw1ryeXb+FX96e6O8/+ot6PwB9+dn8RRUAVK3MIIWY5KD2sR+Hv7FSHRU1NsIp6RYNr7HEiy8TSWNbFCIXGVdyIbn5crMgwDZbuk9uW7kJrCCpuZpK+GrdTWOs8Of9ESqUTXUREGuA27mf2XTE8PyKfmvHJBJvZzw0HYnuGU="));
		skins.put("elitra-xyf", new Property("textures",
				"eyJ0aW1lc3RhbXAiOjE1NTMzNTMxNzk2NTgsInByb2ZpbGVJZCI6ImE1OTQwMTgzOGZkOTQyNzJiOWVhMGQyOGM1NzgwZTY4IiwicHJvZmlsZU5hbWUiOiJrb2t1bWluIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83YzMxNmQ3Y2RkMTI0ZDM1Y2Q0NDZiZWRkNmIwMWZkMTM0ZGNhYjVjNzlkMTc4MThmM2QyNjEwOTQzNjdjM2ZiIn19fQ==",
				"tznLigVDYzOnLGB0QoQnz3dUu+2YkNVhpxzbemBSx73iV18kUt4w6HYXlF2nmTTYhjU2FAxBgAg0HOV7NT5XhDYIm7UaOZEIcq1uRSJiM802wrVEgJcAUe28jb8Es42s7RzJhl903gyKZj2pq2NfZUC2WYhMoMDwtHiBo6AkdDbYFwGieGwjiR+xivmgcE24idXQTU4XiOKHYNbp+i/0G+6cqN3PY1GOr864+1/8ZNxaxsTOi1OT4QED72P29tgDPcuJf4n5AzoeVZgAWmmNe1l4GXRX28FWtDUHpiCAbt6Ckgrm4xZax0xfRQoPsFnsT6C4kXM954AdNusCbOzJRrobor0p7diyU0IqnYZlHUfYKaxsQhPcNiezEsXw/tszaJeXDNEt/OhXgbROvJUI6HnUKv3Kh0giGUkc4hIe57tFKaOSLl+24y17VbRJWTfmu2FSbSmZ2npdnZX5eFeN0tPPt5h+OUbJgCf1t1w2hyiPZ2lUHC+/bIH2sxJl+I84hafvk9t/BSB4kkl+oxCurBvS41X0bCACfqpo5cpdGVSNQgqVZOH20hQNiPMCE0+CSRMlMgkSyHHimULCk5KDN7T/Koa5QZ5AVNbRm48wum8swv+I5q2xhXejy+8jzP1e2yDYambFeOwnCVqf/piEMB9MivgLK8G2A3BrDxM0xOk="));
		skins.put("frostboy", new Property("textures",
				"eyJ0aW1lc3RhbXAiOjE1NTMzNTMyMTcwMjcsInByb2ZpbGVJZCI6ImE1OTQwMTgzOGZkOTQyNzJiOWVhMGQyOGM1NzgwZTY4IiwicHJvZmlsZU5hbWUiOiJrb2t1bWluIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80NjFmYTRjOTEwNmU0ZDNhMTJiMGM2MmEzZmQzOGU4NGY2MjdkMjZiMDJlNjA3YTA4MjU5MDExNjY5ODA2Y2VmIn19fQ==",
				"ZhIiit1PhHcXSo+8GCQWSSiantNRfZYWtjeDBpARrtAs1UsokyZs7ti6ljSgQ3axeGkqjWJVLgsOHOyBaw/eUeDO/HjVEx7ThinWQafTa78BY6j9NUBPHEdtpLBgyl+J+HQBVNdo6ifMVOL6kYS5fThn1Q+h17vP+sZSU6hNtY6BhYcpmCTgXniOwUs7LFGIOXMy6nanjPLKQzUYDHuWh+gZG6TARY3OiTrbTCcnn1WjCiqGcmhxdBVtC8LohxgpZQ0s3HoBGdGN0JXhZ94tWTiPqLeFygDt8rk25ChGWlW97C3clNHowTwFd7bh23pj689ejoo/4z3grTAjgXfLgkaoX6Kcb2D2mf+Nzpn1zPsrB8Ku6ian7PfFGVd2QEu8e0pxPbeZYO3oc3IQyquwTCp/wCeNO8uIQ+u8S1ExcEj852y+3HoVOtAo/SvuZV01KlgGwKhI51baDmeUn5ZtWB8C5gnPQPX0ywUPylnyuS4zOqWU1h+G9/BUM18IrdTel/u6zCwgy4LRcSWETZcS+qM6ekwnGhxwOTp4zeUZJ0sGJmrLsHseF0Ykx7NeJhOs1MeGPZWoBGv5mFheTAMnSWj+MsgJXRePI4IVPXYJgVXecT2rLNoUZNuwURHJsJVvkgYUm8H4JSRuTV2yVzrlBZno8O1YIzoDMF/MEWmKRQ0="));
		skins.put("gardener", new Property("textures",
				"eyJ0aW1lc3RhbXAiOjE1NTMzNTMyODc1OTQsInByb2ZpbGVJZCI6ImE1OTQwMTgzOGZkOTQyNzJiOWVhMGQyOGM1NzgwZTY4IiwicHJvZmlsZU5hbWUiOiJrb2t1bWluIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hNzdmMDJkZDdkZmRjZWU1MmJjNmE3YzYzZDg4Njc1NmY1MWMwZGY0OTQyMjdiZWJlOTFkMDEyNDYyYzA2ZWE0In19fQ==",
				"h+I23Bx1M7hmBZg7Oxaz0KVUKhyd3ozR4ubpU18tFu2f9SPx3ehZ8ulZeeHgqBGVQEXnxica8P3Gft790KM+MtI+p9sbe8Wj00zFzlP7YtQ3rMnVrcS9G5C4jV881tzTGCnrF+H/kep+LmFkyN057WiwWmr+RGqcGFPxcBLjDIfhMUHyakddx/jdIZh0LMWncRSkQahZbx9+R03wQbPAiWXNAxky7xM5tELJXHZAF1yK30ilw7x+GTulqwq8jYh0dtAHC8HLiSTiPLEB3ZcbtXXHF2TtRmb2XrQbGBsGKDzA+tDwATCAxnVLptsv3tRTQb8dQDJ1qQCUUQVqUyC/b9ORJ17QaSX/euv99NDsCkQqm3dL5O0jztLXI5y+be0fwb9cZKPM2qOghUQvSlFUFj+qbTEJf17Dt1gvwMCE4f5h4d/a4luE2EZEyZABoZOoR2xo/rWfpVHNKFsl0yA7AITIqWP6KUm9TdtKYVId5QkHn93FM47KjWq6RSIAofEEkC9/FtUwv7QNjLPlTaS1HxHdAeDgxuhNCMD8PkUgg+iMOoHCJIFAiI6uVBqMfHMief8x8jyL8W3pjKy4iEAo/GZbPuKbfCuc/WwmIpp1ZaJUt/Z81UVl+lRdxeVWi3IIHvD0Wfgtp/hoylNdXY6ZAlqFyOTpyTr60zexp/K0mcM="));
		skins.put("janitor", new Property("textures",
				"eyJ0aW1lc3RhbXAiOjE1NTMzNTMzMjI3MDQsInByb2ZpbGVJZCI6ImE1OTQwMTgzOGZkOTQyNzJiOWVhMGQyOGM1NzgwZTY4IiwicHJvZmlsZU5hbWUiOiJrb2t1bWluIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zZGZjNDYyODEyNGFjN2RmZGQ0MGVhMDFlYTZhNGQxNzk4MjNiNTNkZTFkY2Q2OTAyZmQ1YWIwMWZmMzQwMjczIn19fQ==",
				"EYyioZO6zU2WgL5f54FbXYN1jhEFpzyYMZ2PUojDyITE0aGj9aVGJULOdHII7s+oMDA0u9qI8YOfX6yyda2si9lM+XnjMk1uQPcQff6DeBwb5POT2ffmYpQr79nT+iJcwhB4nOtZHhBfYElUjaVNDi3HYh7a20ggDlVQdy4QXK2geNo0AhJQtU2y+zJzWxVg9stiU6pMJOkLla8GRnaBUddYe1p6DkplGrVatu+rfKDg7eUJI6/S6C+k7Kp/Pdj15nn6hiOzQxP57jF30DMR+F2RyM+D4PERbKWrISRzkcZf+ZD9xYFAg5eraYH/O1hNutzwhCXLM/yo4AZtBTZDoBrnJM2/ON4lHukKoTyra3RDy/MLOy03UR99pDpzb+g/8K7ofKb6Zunu0NB+V5kgj41Q9wG6wLyKcxAbWv7rj5qMmCtvhy7ySOlwoBgHN2CM06LdYpXbaAwQTtghORBgrjgw0+5hBinRo8SEi6S4FWsUAqk3Yc7i5r9y4nkFeinmSVGBSw26ICxB5lJnb487F0wqFjtumjRRAVZeE8pAPT5a9YYsrMUSaOmsckbbGkxTxV+icYawQ78GXP0FIGhGYUoaEIvxrpXHMqUFoGDu2VACbmNtpRBkiOeAvVGl302sTP9u2w3L/9vweRWfW3hMHTyt4BD64UWy18B8Fyhq86A="));
		skins.put("kevin", new Property("textures",
				"eyJ0aW1lc3RhbXAiOjE1NTMzNTMzNjA2MDQsInByb2ZpbGVJZCI6ImE1OTQwMTgzOGZkOTQyNzJiOWVhMGQyOGM1NzgwZTY4IiwicHJvZmlsZU5hbWUiOiJrb2t1bWluIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81ZGVmNzhhMGI3YzE5MDBjODIxNDRkYjJhOTQ0N2QxODYyY2E4MTkwZGE1N2Q0N2I4MTQ2ZWI2YzZkNjNlMmQ2In19fQ==",
				"ibqF7w65uOWxF9XPIrbCeDaNZh+UBucCKs97E5/AizB7YBwjfLSeyZFBVu3UN14x2/P2fVNtyLhUpkOj20i2kavG8rXmdJ45WRVnzbeWgHw5Mz0UfI5k65dpvjmZY7iVsBvu82GMYCMYPliahXwSforrt1NQ2eTSAov1T4jbuGBnhffhZoZF5BHA6z76oWoiRNjM6DgyeuNNCsNX1459a11J0m5cdwY/FNHf5ChlEDrbJBrAA8PVP32VXLNROlSIh14rGKG9BphyXM9Es7DB8NI5kSOFehFV6yUsjSwGNOIOl6XhWwJAs/20XJM2f9awPfwu+e5zW7Mlcr+a1+irL4Ngfygb+pmhT1q0Uyodngh31sA0+jOeOAlwMDrS9q3gq75ux6WHFfdXRYOJelqMO3Aj8WlAwXgVIQm6hzotA6Gdugkp3gVAvWhXNb9HUFxz1EVLhTPUZD/dF6o251MaDKLxQ5PEDpn99xT7o99KQAlq1JUXIgzTQT/y5OJyFl1Nn0PVBciLr+R1zeh8aFTAN64oUylQA+bb2dAFQSlZfFS9XpT6+j1kuR2842ddPeleI/fOH645qD0mgMITb80cCc6VhI8JdJdpOzSkTJxY3xPWp8GNDN2p0Rr0FgfBbdrBILQ1CpMX5JKSxjD0UqbM64MjX8PraSNMSzpXeF63E8Y="));
		skins.put("popcorn", new Property("textures",
				"eyJ0aW1lc3RhbXAiOjE1NTMzNTMzOTQ1MzQsInByb2ZpbGVJZCI6ImE1OTQwMTgzOGZkOTQyNzJiOWVhMGQyOGM1NzgwZTY4IiwicHJvZmlsZU5hbWUiOiJrb2t1bWluIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8xODUwZGU0MTVlYjU4ZTZiZGM0YWQ2N2Y2YjIzNTk5N2VhZTc5MzY4NWU5ZjIwMWYwY2UwYTdkMDgyMTczMmNhIn19fQ==",
				"JA9dhpC1JgdKE0MXK3pUZNBFhD4+zw8FIPNoSMXc9+BioNme8w8POeVCLWvvEnbp+485NLhSsCOj/ziUtfUaIXnfcGrl2FNwrWAIoCyyIyX2YU0zwzG4v5c3yKTjJFFgJ8GtcLCkXuNuBDebisV/KbVPiLdV5T9rwtBtp6D1nt2pjwTwrxkhfKrn+aGLu9MJncb80s/GOYBrUs9x9SUIyoqsSQmTdylpktELB8BRud9M9l8CYbxT1CN/ixzHk8fBO3AiLmJbjyOQO6EezCzzKBovvZnEMwfl7IzwJGbsANbJoX1mx07Q3zYOyJHGXvyIJhXGERl6KrkzuCkuJBDU4PeOZlM8fSstY/PCMrf6sxLN2/urcTbPuRsfDwkboHhYWLJez+DP+U21y0Ij5nJPsSa2FWVAXCKAN4H1RvqM0/R+Ru4Qbifxgm2Ch+xET/5/M9URjzNPP1ByA/XwmZK361YmpfTiPffRjcRxLpxz8LKmBlqUnDZIXq5xmGGtaBh48ccZxmB8HIkAuuzXvajxX/EX+bIeWL9cRHSYrHsEJxSeOMKLFes4qWFIvTvNAHo8iguBUgksWB5AoJbPgiZVr7u8KJdjJWdOc5M0HapZvfSbci9oA5/MP7FJMANPIJmqIBrKz33LK27PhaPyuH7ZsM1yvPaI2g09hlWz4QpopOo="));
		skins.put("tidewavegirl", new Property("textures",
				"eyJ0aW1lc3RhbXAiOjE1NTMzNTM0MzI0MjcsInByb2ZpbGVJZCI6ImE1OTQwMTgzOGZkOTQyNzJiOWVhMGQyOGM1NzgwZTY4IiwicHJvZmlsZU5hbWUiOiJrb2t1bWluIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lZjE3YTM3NzU4ZGFmYTRjYzNmNzcwM2YzZTlmNzVjMmUwM2YyOGYxN2IyNzQwMmNjOGYzMzdhZjkwZDQxNzcxIn19fQ==",
				"bcFA+j0251rhc/nndt9GTNVwjCr8uXZm8246QfmXyn4x/1BGtL66eJfh1Eypwm7ARicYKQNxqR0X2sTp5ErDa+5bN8Fq5Dvvl1Z8oFnhDqEopmCaozmJG0lGxqbI7r5wSdTZBdLgdTP/wJC2HLBKAcs5pgYyg+/IulvuiIfcBXrh174w+h+/o9O98A818Q8dNgYieLkWSGPrFRoZp2RvAZrWKBOzc+bEibTQ/8CBihjoXzB5H1poXw2iJmBJMn+7DLa9fwvX56eoi/cOvg7twz1DPYjG5UZfZtbCxSWWSgwb1K4Ml/1XtF+LvCCUMFJV5Tdy8bpE+JEIGoHal53Zo4erlTKHtNBYvX0/kAk0g1xLhmq2WhhxDfseQAUgAc3i+WV+84ZtYeH9DEPHwqUjsrgBCju0Mcgyns0bJR3bxM4uMz1lCTnsYGwSwWN5ID98g5J/dxvI1RPKlJSNE/HvRsJA1xqNY9heYoyOsSEMQhM8RxkNqqafLRq5Ru1i8tBcrsS43TV4SY4EzBn+FSNF9c+CmdNXeQ6nNAQ+I9Lap0pbcxPPtNYmgtO0q2Prsr88ZOqiFBCePtKiZy/AX7fDZBCm1g3sCOkOh4oI9SvIIcHi0EqBczIUGoXywx8g3/9RqFi1MR06pnaHXnQqnvnQ3FYFDE0s+Sb/tdnKdqySeXY="));
		skins.put("veteran-m", new Property("textures",
				"eyJ0aW1lc3RhbXAiOjE1NTMzNTM0Nzg4ODUsInByb2ZpbGVJZCI6ImE1OTQwMTgzOGZkOTQyNzJiOWVhMGQyOGM1NzgwZTY4IiwicHJvZmlsZU5hbWUiOiJrb2t1bWluIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81YzQwOGM4Yjk4OWE4NjZlOWU2NzUxMWExMzM2MDllYzUxMjhjY2Y4YTA4NjE0OTY5NjFkNGRjN2U4N2QxOTJlIn19fQ==",
				"tDyqm97pSkE0rzE7Bhi7/FjPrHwfEe2eThGXmcsuQR0VyQxRjTWHuVWLnzrBXclxXVF6CJzLWTGfThEcp8IcbiYoFosYBN8e86pD/ro96oYko872eUr909JdjmVWWqNgaBjYOrD+HcQvFR68PVvvvBDLMUcRoi6DufzS3ZezEeA5DVcM/SHHFUPDzBkgb5H1tTIy9E4n1dIPWpw9LcUstXOC9lfwgcgrohcIoyn4Ysa0NWbtn6SE/iqYdp04dWu3qUlWeXz7/KylsteGt+J7ePw97QQCq4lDGFV/WdFDOktFGuA/HngY40FKaEXuL6J9Nw3IUw27durg//VJQ7WWdEu0wWtO4AtmD/W/FRpNkUu2CO+SkTFb20HH8B7C3Te0toZV4tl98sYaUUJ3Rtnj8d4OIrhlg7MhN+K7TXHxmGMwgSQ6MIkRrunY4cNKayTzTq3Ez6SBBxOGpDUvHMkhj+xw11dH5uSU+bi5D7jxlDcOFhEQiG83j4Q8ujsysNZMYRe4syeR3DnAIeYOwlifij+n5OxosxekjAVXaourJ2qE2VyskA1YRnvZN1MqLWU9eFJ00eoeqgzqN/jRB16E4fOJNNqEbAe0Gj9YhA1BXTyi5dTTSMaB1nNuHrjjMFuDrRZ9SDso2O/oRpdhVH7DO7Vxc+mmwMMaJv4I398ih0A="));
		skins.put("weepwithme", new Property("textures",
				"eyJ0aW1lc3RhbXAiOjE1NTMzNTM1MzAwMTksInByb2ZpbGVJZCI6ImE1OTQwMTgzOGZkOTQyNzJiOWVhMGQyOGM1NzgwZTY4IiwicHJvZmlsZU5hbWUiOiJrb2t1bWluIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lYjAyZTg1YTY5ZDBmNTU5ZmZhMzg0Mjk0MDU4NTM1ZDE0MzU3YTIzZjYxYjVkY2ViODcxN2QzZGU4MDFlN2I5In19fQ==",
				"bkaU9buLLGORGUSGMeQGH75ywuaxJgDKZGuJp47LNd1kPrhTR7RzOZx/u3lRwg669LCzvRefR6ju8E5tNIuuIrkDkAJb/VpsvXHl/sDgdNkjR1eT4/PD6N/SmXUx01KPxNCvL4zE/wguNZctOYptR2yHrrjZGusznHXU1UmAIoToNd9oFhVJyCNTW00JmwN+YiB92o/HjihvLu/U8mQAw3xIBdymPlRzaDVaKLOrbmyQ8aUtVz2sugbHIYdAKws4GXzgfLV7L54oBStVnl8uOLmXp+ZAjglnLr4Vu0VCymyhfh3B81fDrgR3A9iWioQs/KC7rlQ4TlAbmvPpJ290oIepZkUINzWKJ+qBbkW2YGQMjojajUQe8r2L6+rtz7ZhoCfLCVG97T9/1pR7C8i4qMqN2Fn0FZsagNmtVfOMYBhsobs3TGyCiMJVPUBkVUaTMDPe5Nyi6/U2M9JAXzWNKlbs0nSB6eFg6AB0PF35teZ0psi0avaFhgfP6qoBkc7fm1UHQqdn2mczlzLuQ88pfIukGH01pxwRziId7uON82RSlUOQUfEWNwSbBzZlyOOho5j5kmgsbxz/g+9L9JDyas04gSV8CVL/Fd6mIS/m/sRiXc5Exgp35ws3W5q8P9pspIfiBO3jdyqfnW64rSGsjgtzUfiQjkCkQ+nvP/geDQ0="));
		skins.put("worker01", new Property("textures",
				"eyJ0aW1lc3RhbXAiOjE1NTMzNTM1NjU1MjksInByb2ZpbGVJZCI6ImE1OTQwMTgzOGZkOTQyNzJiOWVhMGQyOGM1NzgwZTY4IiwicHJvZmlsZU5hbWUiOiJrb2t1bWluIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80YjljYzRhNjkzMjM3ZWViNjA2OGQ5ZGJmODdjNzA5NGI5MjI2OTBhMjNjMjNhZTVlMzk4MDI4YTZkYTIwNjMzIn19fQ==",
				"nRsleOLxCliR4CVh5muwOZsyxjvpiiQV4Toxa9o2pSiMOXd8lB7L9QVgosVwcLMbBjo34SgGX+0uU+RMPPScAO76FgW0lTnqDWHZJfK0s4XZoQA8k9rn9AV5SXnYDUg7juD7FEcfHdL8JzGnDbBQht0mU/VNkjSsxJEdsbuG9xGUrms+Zs4NtXIACh4BWEBtb/iMNkQxB3OSTyKDdg0WK1LHlq5+nWUUnMDoGK+ZZ4DNhAs2xTQKh5EH8GQRjKXYilhpi95Qp1CQ0O229+bzsI/aaSnuXSMs3h+iEH2e3r4Y0ckJGztAgJGIdnEUexw7/29aU3qSIJXwNSc8hOOqtDIZnObGdrSNM62LnLCly1bmGS6vsIOP6U+t6WA32jrbxE9nXubIxrRfQfYkBT/FbGfV3rVgYDivY3pPLBWeI26ctuWfDTX6buW/vAd04eyY0H5IsIzbdOd7uFcfJwYDiu0RN3aJUyEfmKIyC2f+ZnWIJdUdSTUNtgXhGwl6BpvWSdl17rMEkvcd9eSPKh6QYaE11coR6JKY/JFB50hbyelItDpYcSMePDetpTxVIlAyHBuSlvNGsNys+FkD+Y/hhE3EYO38NiPDg0REaE7byO48h4woGf/VUJZydr8W4Dszoj/Xu3iO6vFw4TccpPE7OMjE+qjOfHJJ0uPooRO7+UY="));
		skins.put("worker02", new Property("textures",
				"eyJ0aW1lc3RhbXAiOjE1NTMzNTM1OTgwNjEsInByb2ZpbGVJZCI6ImE1OTQwMTgzOGZkOTQyNzJiOWVhMGQyOGM1NzgwZTY4IiwicHJvZmlsZU5hbWUiOiJrb2t1bWluIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8xYjBiNmI2N2QyZjViNWVkOWUwZTI3YzFmMTFmNGJjNjI4MDAyM2I5Y2YxZmI2YjZlMTM4OTY5MmNjZTc3YzIxIn19fQ==",
				"a3gH/XVeIrVnEVKdeS/ElEmKNaTswBTJrlHFxnpjH78hKZZuUn/RWnoIkff4PdioNVR9pBLq6ARWQsUBSkjFNgN0jxmiQR1ClX9+xQzH5CQKWLAp44KxLauZJlgzQuHWLiGQ4oDrvfzXyGgQMJIlhQQPTXWr7s/qdJIN/gC2mpL6xi//MBzMV37f2FqOux3sCPXMVqpLspKk0SPLwjbhbUOCc0BnBPlPciWMgZxuw0bQ4zUE6LhYPOQSrlU2PsybHoH+LP3jznSNKzRiB4I211ZAxDUwGpr74oVr+u9YhdSvCQ5dZf0sNDfkxjk2GIlIJS/9GQQjulp6PHTHgUZ0wcHJIvBh9fqb+BeQ7SYWzJ9s4+Tmdu861Tp2VHodxUp656iGT+gvQZQ/7E5fk2W7wrZwNtpbwYLsNvAeI8c7IxGt1mAg+isVh5jwMara+A+ECtNHtinHF1ixul/nWwRzSKFJXgyV/0CW1VlNXIHaPWOGBuo4a7ov2P4I+e/n/9VTDmkxUpz8CqpkdwwEe1HzsQqQLL5ODLtdwQQQmYNIVN1oQrxpiFVcCPfvJcjUszLM9LVrIOEpGch+86PuKfNR56ynZJlwDNCZgKJEdjpQZVxPb7v/gBHOcaZ28ALJl6MnkTuNW1HH+p+S0iguVK/1zYjQxJopIwWQjduDt7D5/vQ="));
		nicknamesUsed = new HashMap<>();
		displayNames = new HashMap<>();
		playerListNames = new HashMap<>();
		properties = new HashMap<>();
		// entityPlayers = new HashMap<>();
		PacketHandler.registerPacketListener(nickListener);
		Bukkit.getServer().getPluginManager().registerEvents(nickListener, SoupPvPPlugin.getInstance());
	}

	public static void shutdown() {
		PacketHandler.unregisterPacketListener(nickListener);
		for (Player all : Bukkit.getOnlinePlayers()) {
			unNick(all, true);
		}
	}

	public static boolean isNicked(Player p) {
		return nicknamesUsed.containsKey(p);
	}

	public static String getNickname(Player p) {
		return nicknamesUsed.get(p);
	}

	public static void setNickname(Player p, String name) {
		if (name.length() > 16 || name.length() < 1) {
			return;
		}
		nicknamesUsed.put(p, name);
		Random random = new Random();
		String key = skins.keySet().toArray(new String[] {})[random.nextInt(skins.size())];
		properties.put(p, skins.get(key));
		displayNames.put(p, p.getDisplayName());
		playerListNames.put(p, p.getPlayerListName());
		p.setDisplayName(name);
		p.setPlayerListName(name);
		updateView(p);
	}

	public static void unNick(Player p, boolean updateView) {
		if (!isNicked(p)) {
			return;
		}
		p.setDisplayName(displayNames.get(p));
		p.setPlayerListName(playerListNames.get(p));
		displayNames.remove(p);
		playerListNames.remove(p);
		nicknamesUsed.remove(p);
		properties.remove(p);
		if (updateView) {
			updateView(p);
		}
	}

	public static void updateView(Player p) {
		PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
		PacketPlayOutPlayerInfo playerInfoRemove = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER,
				((CraftPlayer) p).getHandle());
		PacketPlayOutPlayerInfo playerInfoAdd = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER,
				((CraftPlayer) p).getHandle());
		connection.sendPacket(playerInfoRemove);
		connection.sendPacket(playerInfoAdd);
		((CraftServer) Bukkit.getServer()).getHandle()
				.moveToWorld(((CraftPlayer) p).getHandle(), ((CraftWorld) p.getWorld()).getHandle().dimension, true, p.getLocation(), true);
		for (Player all : Bukkit.getOnlinePlayers()) {
			if (all.canSee(p)) {
				all.hidePlayer(p);
				all.showPlayer(p);
			}
		}
	}

	public static HashMap<Player, String> getNicknamesUsed() {
		return nicknamesUsed;
	}

	public static HashMap<Player, String> getDisplayNames() {
		return displayNames;
	}

	public static String getName(Player p) {
		if (isNicked(p)) {
			return getNickname(p);
		} else {
			return p.getName();
		}
	}

	public static HashMap<Player, Property> getProperties() {
		return properties;
	}

	public static boolean isNicknameUsed(String nick) {
		for (String name : nicknamesUsed.values()) {
			if (name.equalsIgnoreCase(nick)) {
				return true;
			}
		}
		return false;
	}

	// public static HashMap<Player, EntityPlayer> getEntityPlayers() {
	// return entityPlayers;
	// }

}
