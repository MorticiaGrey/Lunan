local groups
if args[1] == nil then
    groups = lunan.getGroups(lunan.getCurrUser())
else
    groups = lunan.getGroups(args[1])
end

if groups == nil then
    print("error: user not found")
    return
end

if not (groups == nil) then
    local buffer = ""
    for i in pairs(groups) do
        buffer = groups[i] .. " " .. buffer
    end
    if not (buffer == "") then
        print(buffer)
    end
end
