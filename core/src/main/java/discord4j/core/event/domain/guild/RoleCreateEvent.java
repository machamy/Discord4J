/*
 * This file is part of Discord4J.
 *
 * Discord4J is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Discord4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Discord4J.  If not, see <http://www.gnu.org/licenses/>.
 */
package discord4j.core.event.domain.guild;

import discord4j.core.event.domain.Event;
import discord4j.core.object.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Role;
import reactor.core.publisher.Mono;

public class RoleCreateEvent implements Event {

    private final long guildId;
    private final Role role;

    public RoleCreateEvent(long guildId, Role role) {
        this.guildId = guildId;
        this.role = role;
    }

    public Snowflake getGuildId() {
        return Snowflake.of(guildId);
    }

    public Mono<Guild> getGuild() {
        throw new UnsupportedOperationException("Not yet implemented...");
    }

    public Role getRole() {
        return role;
    }
}
