if args[1] == nil then
    print("usage: trigger [EVENT] [ARGS...]")
    return
end
local event = args[1]
table.remove(args, 1)
lunan.triggerEvent(event, args)
