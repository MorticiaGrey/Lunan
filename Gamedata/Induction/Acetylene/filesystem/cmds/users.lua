local users = lunan.getUsers()
local buffer = ""
for i in pairs(users) do
    buffer = buffer .. " " .. users[i]
end
print(buffer)
