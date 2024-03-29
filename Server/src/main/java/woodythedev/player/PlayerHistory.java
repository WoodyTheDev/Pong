package woodythedev.player;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import woodythedev.pong.game.Game;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PlayerHistory {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    public Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    public Game game;

    @CreationTimestamp
    private LocalDateTime createDate;
}
